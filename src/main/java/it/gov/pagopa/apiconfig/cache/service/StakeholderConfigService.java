package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import it.gov.pagopa.apiconfig.cache.model.node.CacheSchemaVersion;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.CacheSchemaVersionDeserializer;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import it.gov.pagopa.apiconfig.cache.util.JsonSerializer;
import it.gov.pagopa.apiconfig.cache.util.JsonToXls;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.gov.pagopa.apiconfig.starter.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Service
@Transactional
public class StakeholderConfigService {

    @Value("${info.application.version}")
    private String APP_VERSION;

    @Value("${xls.mask-passwords}")
    private boolean xlsMaskPasswords;

    @Value("${in_progress.ttl}")
    private long IN_PROGRESS_TTL;

    @Autowired private CacheController cacheController;

    @Autowired private RedisRepository redisRepository;

    @Autowired private CacheRepository cacheRepository;

    @Autowired private JsonSerializer jsonSerializer;

    @Autowired private CacheKeyUtils cacheKeyUtils;

    public ConfigData loadCache(String stakeholder, String schemaVersion) throws IOException {
        log.info(String.format("Loading on Redis %s cache", getStakeholderWithSchema(stakeholder, schemaVersion)));
        // verify if the id of full cache and stakeholder cache are the same
        byte[] stakeholderCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(getStakeholderWithSchema(stakeholder, schemaVersion)));
        byte[] fullCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL));
        if (stakeholderCacheId != null && Arrays.equals(stakeholderCacheId, fullCacheId)) {
            // retrieve stakeholder cache
            return getCacheFromRedis(stakeholder, schemaVersion);
        }
        return null;
    }

    public ConfigData getCache(String stakeholder, String schemaVersion, String[] keys) throws IOException {
        // retrieve configDataVersion from Redis
        ConfigData configData = loadCache(stakeholder, schemaVersion);

        if (configData == null) {
            // retrieve full cache and generate configDava
            configData = generateCacheSchemaFromInMemory(stakeholder, schemaVersion, keys);
        }

        return configData;
    }

    private ConfigData generateCacheSchemaFromInMemory(String stakeholder, String schemaVersion, String[] keys) throws IOException {
        // retrieve full cache and generate configDava
        HashMap<String, Object> inMemoryCache = cacheController.getInMemoryCache();
        HashMap<String, Object> clonedInMemoryCache = (HashMap<String, Object>)inMemoryCache.clone();

        String xCacheId = (String)clonedInMemoryCache.getOrDefault(Constants.VERSION, Constants.NA);
        ZonedDateTime utcDateTime = (ZonedDateTime) clonedInMemoryCache.get(Constants.TIMESTAMP);
        String xCacheTimestamp = DateTimeUtils.getString(utcDateTime);
        String xCacheVersion = getGZIPVersion(schemaVersion);

        // generate v1 cache version
        clonedInMemoryCache.put(Constants.VERSION, xCacheId);
        clonedInMemoryCache.put(Constants.CACHE_VERSION, xCacheVersion);
        clonedInMemoryCache.put(Constants.TIMESTAMP, xCacheTimestamp);
        CacheSchemaVersion cacheSchemaVersion = null;
        if (schemaVersion.equals("v1")) {
            cacheSchemaVersion = cacheToConfigDataV1(clonedInMemoryCache, keys);
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

        byte[] cacheByteArray = compressJsonToGzip(configData);

        log.info(String.format("saving on Redis %s %s", actualKey, actualKeyV1));
        redisRepository.pushToRedisAsync(actualKey, actualKeyV1, cacheByteArray, cacheSchemaVersion.getVersion().getBytes(StandardCharsets.UTF_8));

        return configData;
    }

    public void saveOnDB(ConfigData configData, String schemaVersion) {
        log.info("saving on CACHE table " + configData.getXCacheId());
        try {
            String cacheVersion = getGZIPVersion(schemaVersion);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            HashMap<String, Object> cloned = objectMapper.convertValue(configData.getCacheSchemaVersion(), HashMap.class);
            cacheRepository.save(Cache.builder()
                    .id(configData.getXCacheId())
                    .cache(jsonSerializer.serialize(cloned))
                    .time(ZonedDateTime.parse(configData.getXCacheTimestamp()))
                    .version(cacheVersion)
                    .build());

            log.info("saved on CACHE table " + configData.getXCacheId());
        } catch (Exception e) {
            log.error("[ALERT] could not save on db", e);
        }
    }

    public CacheVersion getVersionId(String stakeholder, String schemaVersion, String[] keys) throws IOException {
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

        throw new AppException(AppError.CACHE_NOT_INITIALIZED);
    }

    public byte[] getXLSX(String stakeholder, String schemaVersion) {
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

        throw new AppException(AppError.CACHE_NOT_INITIALIZED);
    }

    private String getStakeholderWithSchema(String stakeholder, String schemaVersion) {
        return String.format("%s_%s", stakeholder, schemaVersion);
    }

    public ConfigDataV1 cacheToConfigDataV1(Map<String,Object> inMemoryCache, String[] keys) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Set<String> keysSet = new HashSet<>(Arrays.asList(keys));
        inMemoryCache.keySet().removeIf(key -> !keysSet.contains(key));
        return objectMapper.convertValue(inMemoryCache, ConfigDataV1.class);
    }

    private ConfigData getCacheFromRedis(String stakeholder, String schemaVersion) throws IOException {
        byte[] bytes = redisRepository.get(cacheKeyUtils.getCacheKey(getStakeholderWithSchema(stakeholder, schemaVersion)));
        return bytes == null ? null : decompressGzipToConfigData(bytes, schemaVersion);
    }

    private String getGZIPVersion(String schemaVersion) {
        String version = String.format("%s-%s-%s", Constants.GZIP_JSON, schemaVersion, APP_VERSION);
        if (version.length() > 32) {
            return version.substring(0, 32);
        }
        return version;
    }

    private static ConfigData decompressGzipToConfigData(byte[] gzipBytes, String schemaVersion) throws IOException {
        byte[] unzipped = ZipUtils.unzip(gzipBytes);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CacheSchemaVersion.class, new CacheSchemaVersionDeserializer(getCacheSchemaVersionClass(schemaVersion)));
        objectMapper.registerModule(module);
        JsonParser jsonParser = objectMapper.getFactory().createParser(unzipped);
        ConfigData configData = objectMapper.readValue(jsonParser, ConfigData.class);
        jsonParser.close();
        return configData;
    }
    public static byte[] compressJsonToGzip(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // use GZIPOutputStream to compress data
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream);
                OutputStreamWriter writer = new OutputStreamWriter(gzipOut)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(writer, object);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static Class getCacheSchemaVersionClass(String schemaVersion) {
        switch (schemaVersion) {
            case "v1":
                return ConfigDataV1.class;
            default:
                throw new AppException(AppError.CACHE_SCHEMA_NOT_VALID);
        }
    }


}
