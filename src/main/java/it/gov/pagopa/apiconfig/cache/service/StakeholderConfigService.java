package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.JsonSerializer;
import it.gov.pagopa.apiconfig.cache.util.JsonToXls;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.gov.pagopa.apiconfig.starter.repository.CacheRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Service
@Transactional
public class StakeholderConfigService {

    @Value("${info.application.version}")
    private String APP_VERSION;

    @Value("#{'${canary}'=='true' ? '_canary' : ''}")
    private String keySuffix;

    @Value("#{'${sendEvent}'=='true'}")
    private Boolean sendEvent;

    @Value("${xls.mask-passwords}")
    private boolean xlsMaskPasswords;

    @Value("${in_progress.ttl}")
    private long IN_PROGRESS_TTL;

    @Autowired private CacheController cacheController;

    @Autowired private RedisRepository redisRepository;

    @Autowired private CacheRepository cacheRepository;

    @Autowired private JsonSerializer jsonSerializer;

    @Autowired private CacheKeyUtils cacheKeyUtils;

    public ConfigData loadCache(String stakeholder) throws IOException {
        log.info(String.format("Loading on Redis %s cache", stakeholder));
        // verify if the id of full cache and stakeholder cache are the same
        byte[] stakeholderCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholder));
        byte[] fullCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL));
        if (stakeholderCacheId != null && Arrays.equals(stakeholderCacheId, fullCacheId)) {
            // retrieve stakeholder cache
            return getCacheFromRedis(stakeholder);
        }
        return null;
    }

    public ConfigData getCache(String stakeholder, String schemaVersion, String[] keys) throws IOException {
        stakeholder = getStakeholderWithSchema(stakeholder, schemaVersion);
        // retrieve configDataVersion from Redis
        ConfigData configData = loadCache(stakeholder);

        if (configData == null) {

            // retrieve full cache
            HashMap<String, Object> inMemoryCache = cacheController.getInMemoryCache();
            HashMap<String, Object> clonedInMemoryCache = (HashMap<String, Object>)inMemoryCache.clone();

            String xCacheId = (String)clonedInMemoryCache.getOrDefault(Constants.VERSION, Constants.NA);
            String xCacheTimestamp = DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime)clonedInMemoryCache.get(Constants.TIMESTAMP));
//            String xCacheVersion = (String)clonedInMemoryCache.getOrDefault(Constants.CACHE_VERSION, Constants.NA);
            String xCacheVersion = getGZIPVersion(schemaVersion);

            // generate v1 cache version
            ConfigDataV1 configDataV1 = cacheToConfigDataV1(clonedInMemoryCache, keys);

            configData = ConfigData.builder()
                    .configDataV1(configDataV1)
                    .xCacheId(xCacheId)
                    .xCacheTimestamp(xCacheTimestamp)
                    .xCacheVersion(xCacheVersion)
                    .build();

            // save cache on redis
            String actualKey = cacheKeyUtils.getCacheKey(stakeholder);
            String actualKeyV1 = cacheKeyUtils.getCacheIdKey(stakeholder);

            byte[] cacheByteArray = compressJsonToGzip(configData);

            log.info(String.format("saving on Redis %s %s", actualKey, actualKeyV1));
            redisRepository.pushToRedisAsync(actualKey, actualKeyV1, cacheByteArray, configDataV1.getVersion().getBytes(StandardCharsets.UTF_8));
        }

        return configData;
    }

    public void saveOnDB(ConfigData configData, String schemaVersion) {
        log.info("saving on CACHE table " + configData.getXCacheId());
        try {
            String cacheVersion = String.format("%s-%s-%s", Constants.GZIP_JSON, schemaVersion, APP_VERSION);
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap<String, Object> cloned = objectMapper.convertValue(configData.getConfigDataV1(), HashMap.class);
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

    public CacheVersion getVersionId(String stakeholder, String schemaVersion) {
        stakeholder = getStakeholderWithSchema(stakeholder, schemaVersion);
        byte[] stakeholderCacheId = redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholder));
        if (stakeholderCacheId != null) {
            return CacheVersion.builder()
                    .version(new String(stakeholderCacheId, StandardCharsets.UTF_8))
                    .build();
        }
        throw new AppException(AppError.CACHE_NOT_INITIALIZED);
    }

    public byte[] getXLSX(String stakeholder, String schemaVersion) {
        stakeholder = getStakeholderWithSchema(stakeholder, schemaVersion);
        ConfigData configData = null;
        try {
            configData = getCacheFromRedis(stakeholder);
        } catch (IOException e) {
            throw new AppException(AppError.CACHE_NOT_READABLE);
        }
        if (configData != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> configDataV1 = objectMapper.convertValue(configData.getConfigDataV1(), new TypeReference<Map<String, Object>>() {});
            return new JsonToXls(xlsMaskPasswords).convert(configDataV1);
        }

        throw new AppException(AppError.CACHE_NOT_INITIALIZED);
    }

    private String getStakeholderWithSchema(String stakeholder, String schemaVersion) {
        return String.format("%s_%s", stakeholder, schemaVersion);
    }

    private ConfigDataV1 cacheToConfigDataV1(Map<String,Object> inMemoryCache, String[] keys) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Set<String> keysSet = new HashSet<>(Arrays.asList(keys));
        inMemoryCache.keySet().removeIf(key -> !keysSet.contains(key));
        return objectMapper.convertValue(inMemoryCache, ConfigDataV1.class);
    }

    private ConfigData getCacheFromRedis(String stakeholder) throws IOException {
        byte[] bytes = redisRepository.get(cacheKeyUtils.getCacheKey(stakeholder));
        return bytes == null ? null : decompressGzipToConfigData(bytes);
    }

    private String getGZIPVersion(String schemaVersion) {
        String version = String.format("%s_%s-%s", Constants.GZIP_JSON, schemaVersion, APP_VERSION);
        if (version.length() > 32) {
            return version.substring(0, 32);
        }
        return version;
    }

//    private void setCacheV1InProgress(String stakeholder) {
//        String actualKeyV1 = getKeyV1InProgress(stakeholder);
//        redisRepository.save(actualKeyV1, "1".getBytes(StandardCharsets.UTF_8), IN_PROGRESS_TTL);
//    }
//

    private static ConfigData decompressGzipToConfigData(byte[] gzipBytes) throws IOException {
        byte[] unzipped = ZipUtils.unzip(gzipBytes);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(unzipped);
        ConfigData configData = objectMapper.readValue(jsonParser, ConfigData.class);
        jsonParser.close();
        return configData;
    }
    private static byte[] compressJsonToGzip(Object object) throws IOException {
        // Creare un ByteArrayOutputStream per memorizzare il risultato
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        // Usare GZIPOutputStream per comprimere i dati
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream);
             OutputStreamWriter writer = new OutputStreamWriter(gzipOut)) {

            // Serializzare l'oggetto in JSON e scriverlo nel flusso compresso
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.writeValue(writer, object);
        }

        // Ottenere il risultato come byte[]
        return byteArrayOutputStream.toByteArray();
    }


}
