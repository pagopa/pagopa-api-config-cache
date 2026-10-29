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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Service
@Transactional
public class StakeholderConfigService {

    private final String appVersion;
    private final boolean xlsMaskPasswords;
    private final boolean cacheSchemaLockAllStakeholder;
    private final CacheConfigService cacheConfigService;
    private final RedisRepository redisRepository;
    private final CacheKeyUtils cacheKeyUtils;
    /** Centralized mapper for the per-stakeholder cache (write / convert / read). */
    private final ObjectMapper cacheObjectMapper;

    public StakeholderConfigService(
            @Value("${info.application.version}") String appVersion,
            @Value("${xls.mask-passwords}") boolean xlsMaskPasswords,
            @Value("${cache-schema.lock-all-stakeholder}") boolean cacheSchemaLockAllStakeholder,
            CacheConfigService cacheConfigService,
            RedisRepository redisRepository,
            CacheKeyUtils cacheKeyUtils,
            @Qualifier(ObjectMapperConfig.CACHE_OBJECT_MAPPER) ObjectMapper cacheObjectMapper
    ) {
        this.appVersion = appVersion;
        this.xlsMaskPasswords = xlsMaskPasswords;
        this.cacheSchemaLockAllStakeholder = cacheSchemaLockAllStakeholder;
        this.cacheConfigService = cacheConfigService;
        this.redisRepository = redisRepository;
        this.cacheKeyUtils = cacheKeyUtils;
        this.cacheObjectMapper = cacheObjectMapper;
    }

    protected ConfigData loadCache(Stakeholder stakeholder, String schemaVersion) throws IOException {
        String stakeholderWithSchema = getStakeholderWithSchema(stakeholder, schemaVersion);
        log.info("Loading on Redis {} cache", stakeholderWithSchema);
        // verify if the id of full cache and stakeholder cache are the same
        byte[] stakeholderCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholderWithSchema));
        byte[] fullCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL));
        if (stakeholderCacheId != null && Arrays.equals(stakeholderCacheId, fullCacheId)) {
            // retrieve stakeholder cache
            return getCacheFromRedis(stakeholder, schemaVersion);
        }
        return null;
    }

    public ConfigData getCache(Stakeholder stakeholder, String schemaVersion, String[] keys) throws IOException {
        // retrieve configDataVersion from Redis
        ConfigData configData = loadCache(stakeholder, schemaVersion);

        if (configData == null) {
            configData = generateCacheSchemaFromInMemory(stakeholder, schemaVersion, keys);
        }

        return configData;
    }

    private ConfigData generateCacheSchemaFromInMemory(Stakeholder stakeholder, String schemaVersion, String[] keys) throws IOException {
        ConfigData configData;
        String lockedStakeholders = cacheSchemaLockAllStakeholder ? "all" : stakeholder.toString();
        boolean isLocked = saveCacheSchemaGenerationInProgress(lockedStakeholders, stakeholder.toString());
        if (isLocked) {
            log.info("ConfigData will be generated in memory for stakeholder {}. Generation is not permitted for other stakeholders.", stakeholder);
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

            configData = ConfigData.builder()
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

            log.info("saving on Redis {} {}", actualKey, actualKeyV1);
            redisRepository.pushToRedisSync(actualKey, actualKeyV1, cacheByteArray, cacheSchemaVersion.getVersion().getBytes(StandardCharsets.UTF_8));

            // Removing lock on cache schema generation, permitting next schema cache generation
            removeCacheSchemaGenerationInProgress(lockedStakeholders);
            log.info("ConfigData generated in memory for stakeholder [{}]. Generation has been unlocked for other stakeholders.", stakeholder);

        } else {
            log.debug("ConfigData not generated in memory for stakeholder [{}]. Generation is locked by another process.", stakeholder);
            throw new AppException(AppError.CACHE_SCHEMA_NOT_INITIALIZED, stakeholder);
        }
        return configData;
    }

    public CacheVersion getVersionId(Stakeholder stakeholder, String schemaVersion, String[] keys) throws IOException {
        byte[] stakeholderCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(getStakeholderWithSchema(stakeholder, schemaVersion)));
        byte[] fullCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL));
        // check cache validity and generate schema if necessary
        if (stakeholderCacheId != null) {
            String cacheVersion = new String(stakeholderCacheId, StandardCharsets.UTF_8);
            if (fullCacheId != null && !Arrays.equals(stakeholderCacheId, fullCacheId)) {
                generateCacheSchemaFromInMemory(stakeholder, schemaVersion, keys);
                cacheVersion = new String(fullCacheId, StandardCharsets.UTF_8);
            }
            return CacheVersion.builder()
                    .version(cacheVersion)
                    .build();
        }
        else {
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
                throw new AppException(AppError.CACHE_NOT_INITIALIZED, stakeholder.toString());
            }
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

    private String getStakeholderWithSchema(Stakeholder stakeholder, String schemaVersion) {
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

    private boolean saveCacheSchemaGenerationInProgress(String stakeholder, String lockValue) {
        return redisRepository.saveIfAbsent(cacheKeyUtils.getCacheGenerationLockKey(stakeholder), lockValue.getBytes(StandardCharsets.UTF_8), 1);
    }

    private void removeCacheSchemaGenerationInProgress(String stakeholder) {
        redisRepository.remove(cacheKeyUtils.getCacheGenerationLockKey(stakeholder));
    }

}
