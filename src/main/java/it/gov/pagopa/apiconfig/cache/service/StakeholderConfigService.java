package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.config.ObjectMapperConfig;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.cache.model.node.CacheSchemaVersion;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import it.gov.pagopa.apiconfig.cache.util.JsonToXls;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

@Slf4j
@Service
public class StakeholderConfigService {

    private final String appVersion;
    private final boolean xlsMaskPasswords;
    private final Duration lockWaitTimeout;
    private final CacheConfigService cacheConfigService;
    private final RedisRepository redisRepository;
    private final CacheKeyUtils cacheKeyUtils;
    /** Centralized mapper for the per-stakeholder cache (write / convert / read). */
    private final ObjectMapper cacheObjectMapper;
    private final Map<Stakeholder, ReentrantLock> generationLocks = new ConcurrentHashMap<>();

    public StakeholderConfigService(
            @Value("${info.application.version}") String appVersion,
            @Value("${xls.mask-passwords}") boolean xlsMaskPasswords,
            @Value("${cache-schema.lock-wait-timeout:30s}") Duration lockWaitTimeout,
            CacheConfigService cacheConfigService,
            RedisRepository redisRepository,
            CacheKeyUtils cacheKeyUtils,
            @Qualifier(ObjectMapperConfig.CACHE_OBJECT_MAPPER) ObjectMapper cacheObjectMapper
    ) {
        this.appVersion = appVersion;
        this.xlsMaskPasswords = xlsMaskPasswords;
        this.lockWaitTimeout = lockWaitTimeout;
        this.cacheConfigService = cacheConfigService;
        this.redisRepository = redisRepository;
        this.cacheKeyUtils = cacheKeyUtils;
        this.cacheObjectMapper = cacheObjectMapper;
    }

    protected String getStakeholderCacheVersionFromRedisIfUpToDate(Stakeholder stakeholder, String schemaVersion) throws IOException {
        String stakeholderWithSchema = getStakeholderWithSchema(stakeholder, schemaVersion);
        log.debug("Retrieve {} cache version and {} cache version from Redis", stakeholderWithSchema, Constants.FULL);
        // verify if the id of full cache and stakeholder cache are the same
        byte[] stakeholderCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholderWithSchema));
        byte[] fullCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL));
        if (stakeholderCacheId != null && Arrays.equals(stakeholderCacheId, fullCacheId)) {
            log.debug("Stakeholder {} cache already up to date on Redis, retrieve it", stakeholder);
            return new String(stakeholderCacheId, StandardCharsets.UTF_8);
        }
        log.debug("Stakeholder {} cache is not up to date on Redis, retrieve full cache and update it", stakeholder);
        return null;
    }

    public ConfigData getCache(Stakeholder stakeholder, String schemaVersion, String[] keys) throws IOException {
        // fast path without lock: cache already up to date on Redis
        if (getStakeholderCacheVersionFromRedisIfUpToDate(stakeholder, schemaVersion) != null) {
            return getCacheFromRedis(stakeholder, schemaVersion);
        }

        ReentrantLock lock = generationLocks.computeIfAbsent(stakeholder, s -> new ReentrantLock());
        try {
            log.debug("Stakeholder {} try to acquire cache generation lock", stakeholder);
            if (!lock.tryLock(lockWaitTimeout.toMillis(), TimeUnit.MILLISECONDS)) {
                log.warn("Timeout waiting for stakeholder {} cache generation", stakeholder);
                throw new AppException(AppError.CACHE_SCHEMA_NOT_INITIALIZED, stakeholder);
            }
            log.debug("Stakeholder {} cache generation lock acquired", stakeholder);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppException(AppError.CACHE_SCHEMA_NOT_INITIALIZED, e, stakeholder);
        }
        try {
            // another thread may have refreshed the cache while this one was waiting
            if (getStakeholderCacheVersionFromRedisIfUpToDate(stakeholder, schemaVersion) != null) {
                return getCacheFromRedis(stakeholder, schemaVersion);
            }
            log.debug("Stakeholder {} cache is stale on Redis", stakeholder);
            return generateCacheSchemaFromInMemory(stakeholder, schemaVersion, keys);
        } finally {
            log.debug("Stakeholder {} cache generation lock released", stakeholder);
            lock.unlock();
        }
    }

    private ConfigData generateCacheSchemaFromInMemory(Stakeholder stakeholder, String schemaVersion, String[] keys) throws IOException {
        log.debug("Stakeholder {} cache will be generated from in memory cache", stakeholder);
        // retrieve full cache and generate configData
        Map<String, Object> inMemoryCache = cacheConfigService.getInMemoryCacheOrRefreshIfNotExist();

        // extraction of the requested keys
        Set<String> keysSet = new HashSet<>(Arrays.asList(keys));
        HashMap<String, Object> clonedInMemoryCache = new HashMap<>();
        for (String key : keysSet) {
            Object value = inMemoryCache.get(key);
            if (value != null) {
                clonedInMemoryCache.put(key, value);
            }
        }

        String xCacheId = (String)inMemoryCache.getOrDefault(Constants.VERSION, Constants.NA);
        ZonedDateTime utcDateTime = (ZonedDateTime) inMemoryCache.get(Constants.TIMESTAMP);
        String xCacheTimestamp = DateTimeUtils.getString(utcDateTime);
        String xCacheVersion = getGZIPVersion(schemaVersion);

        // generate v1 cache version
        clonedInMemoryCache.put(Constants.VERSION, xCacheId);
        clonedInMemoryCache.put(Constants.CACHE_VERSION, xCacheVersion);
        clonedInMemoryCache.put(Constants.TIMESTAMP, xCacheTimestamp);
        CacheSchemaVersion cacheSchemaVersion = null;
        if (schemaVersion.equals("v1")) {
            cacheSchemaVersion = cacheToConfigDataV1(stakeholder, clonedInMemoryCache);
        } else {
            throw new AppException(AppError.CACHE_SCHEMA_NOT_VALID);
        }

        ConfigData configData = ConfigData.builder()
                .cacheSchemaVersion(cacheSchemaVersion)
                .xCacheId(xCacheId)
                .xCacheTimestamp(xCacheTimestamp)
                .xCacheVersion(xCacheVersion)
                .build();

        // save cache on redis
        String actualKey = cacheKeyUtils.getCacheKey(getStakeholderWithSchema(stakeholder, schemaVersion));
        String actualKeyV1 = cacheKeyUtils.getCacheIdKey(getStakeholderWithSchema(stakeholder, schemaVersion));

        // create temporary file
        byte[] cacheByteArray = compressJsonToGzip(configData);

        redisRepository.pushToRedisSync(actualKey, actualKeyV1, cacheByteArray, cacheSchemaVersion.getVersion().getBytes(StandardCharsets.UTF_8));
        log.info("Stakeholder {} cache generated and saved on Redis", stakeholder);
        return configData;
    }

    public CacheVersion getVersionId(Stakeholder stakeholder, String schemaVersion, String[] keys) throws IOException {
        String cacheVersion = getStakeholderCacheVersionFromRedisIfUpToDate(stakeholder, schemaVersion);
        if (cacheVersion != null) {
            return CacheVersion.builder()
                    .version(cacheVersion)
                    .build();
        }
        try {
            ConfigData configData = getCache(stakeholder, schemaVersion, keys);

            return CacheVersion.builder()
                    .version(configData.getCacheSchemaVersion().getVersion())
                    .build();
        }
        catch (AppException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AppException(AppError.CACHE_NOT_INITIALIZED, e, stakeholder.toString());
        }
    }

    public byte[] getXLSX(Stakeholder stakeholder, String schemaVersion) {
        ConfigData configData = null;
        try {
            configData = getCacheFromRedis(stakeholder, schemaVersion);
        } catch (IOException e) {
            throw new AppException(AppError.CACHE_NOT_READABLE);
        }
        if (configData != null) {
            HashMap<String, Object> inMemoryCacheFormat = new HashMap<>();
            CacheSchemaVersion cacheSchemaVersion = configData.getCacheSchemaVersion();

            ReflectionUtils.doWithFields(cacheSchemaVersion.getClass(), field -> {
                Method method = ReflectionUtils.findMethod(cacheSchemaVersion.getClass(), "get" + StringUtils.capitalize(field.getName()));
                if (method != null) {
                    try {
                        Object object = method.invoke(cacheSchemaVersion);
                        inMemoryCacheFormat.put(field.getName(), Objects.requireNonNullElseGet(object, HashMap::new));
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException("No method found " + field.getName(), e);
                    }
                }
            });
            inMemoryCacheFormat.put(Constants.VERSION, configData.getXCacheId());
            inMemoryCacheFormat.put(Constants.TIMESTAMP, configData.getXCacheTimestamp());
            inMemoryCacheFormat.put(Constants.CACHE_VERSION, getGZIPVersion(schemaVersion));

            return new JsonToXls(xlsMaskPasswords).convert(inMemoryCacheFormat);
        }

        throw new AppException(AppError.CACHE_NOT_INITIALIZED, stakeholder.toString());
    }

    protected String getStakeholderWithSchema(Stakeholder stakeholder, String schemaVersion) {
        return String.format("%s_%s", stakeholder, schemaVersion);
    }

    public ConfigDataV1 cacheToConfigDataV1(Stakeholder stakeholder, Map<String,Object> inMemoryCache) {
        if (Stakeholder.STANDIN.equals(stakeholder)) {
            elaborateStandInCache(inMemoryCache);
        }
        return cacheObjectMapper.convertValue(inMemoryCache, ConfigDataV1.class);
    }

    private void elaborateStandInCache(Map<String,Object> inMemoryCache) {
        // remove station not configured on forwarder
        HashMap<String, Station> stations = (HashMap) ((HashMap) inMemoryCache.get(Constants.STATIONS)).clone();
        stations
                .entrySet()
                .removeIf(
                        entry -> {
                            Station station = entry.getValue();
                            return station.getPofService() == null
                                    || station.getPofService().getPath() == null
                                    || !station.getPofService().getPath().contains("pagopa-node-forwarder");
                        });
        inMemoryCache.put(Constants.STATIONS, stations);
    }


    private ConfigData getCacheFromRedis(Stakeholder stakeholder, String schemaVersion) throws IOException {
        byte[] bytes = redisRepository.get(cacheKeyUtils.getCacheKey(getStakeholderWithSchema(stakeholder, schemaVersion)));
        return bytes == null ? null : decompressGzipToConfigData(bytes, schemaVersion);
    }

    private String getGZIPVersion(String schemaVersion) {
        String version = String.format("%s-%s-%s", Constants.GZIP_JSON, schemaVersion, appVersion);
        if (version.length() > 32) {
            return version.substring(0, 32);
        }
        return version;
    }

    private ConfigData decompressGzipToConfigData(byte[] gzipBytes, String schemaVersion) throws IOException {
        // Validate the requested schema (throws CACHE_SCHEMA_NOT_VALID for unsupported versions),
        // preserving the previous behavior. The cacheObjectMapper already carries the schema-aware
        // CacheSchemaVersion deserializer.
        getCacheSchemaVersionClass(schemaVersion);
        try (InputStream in = ZipUtils.unzipToStream(gzipBytes)) {
            return cacheObjectMapper.readValue(in, ConfigData.class);
        }
    }

    protected byte[] compressJsonToGzip(Object object) throws IOException {
        // Serialize + gzip fully in streaming: the uncompressed JSON is never held in the heap as a whole.
        return ZipUtils.gzip(out -> cacheObjectMapper.writeValue(out, object));
    }

    private static Class<?> getCacheSchemaVersionClass(String schemaVersion) {
        if (schemaVersion.equals("v1")) {
            return ConfigDataV1.class;
        }
        throw new AppException(AppError.CACHE_SCHEMA_NOT_VALID);
    }

}
