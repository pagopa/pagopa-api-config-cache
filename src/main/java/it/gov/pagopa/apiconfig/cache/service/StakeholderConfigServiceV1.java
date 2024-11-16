package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.ConfigData;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.JsonToXls;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
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
public class StakeholderConfigServiceV1 {

    @Value("${info.application.version}")
    private String appVersion;

    @Value("#{'${canary}'=='true' ? '_canary' : ''}")
    private String keySuffix;

    @Value("apicfg_${spring.database.id}_{{stakeholder}}_v1")
    private String keyV1;

    @Value("apicfg_${spring.database.id}_{{stakeholder}}_v1_id")
    private String keyV1Id;

    @Value("apicfg_${spring.database.id}_{{stakeholder}}_v1_in_progress")
    private String keyV1InProgress;

    @Value("#{'${saveDB}'=='true'}")
    private Boolean saveDB;

    @Value("#{'${sendEvent}'=='true'}")
    private Boolean sendEvent;

    @Value("${xls.mask-passwords}")
    private boolean xlsMaskPasswords;

    @Value("${in_progress.ttl}")
    private long IN_PROGRESS_TTL;

    private static String stakeholderPlaceholder = "{{stakeholder}}";

    private String NA = "n/a";

    @Autowired private CacheController cacheController;

    @Autowired private RedisRepository redisRepository;

    public ConfigData loadCache(String stakeholder) throws IOException {
        log.info(String.format("loading on Redis %s cache", stakeholder));
        byte[] bytes = redisRepository.get(getKeyV1(stakeholder));
        return bytes == null ? null : decompressGzipToConfigData(bytes);
    }

    public ConfigData getCache(String stakeholder, String[] keys) throws IOException {
        // retrieve configDataV1 from Redis
        ConfigData configData = loadCache(stakeholder);

        if (configData == null) {

            // retrieve full cache
            HashMap<String, Object> inMemoryCache = cacheController.getInMemoryCache();
            HashMap<String, Object> clonedInMemoryCache = (HashMap<String, Object>)inMemoryCache.clone();

            String xCacheId = (String)clonedInMemoryCache.getOrDefault(Constants.VERSION, NA);
            String xCacheTimestamp = DateTimeFormatter.ISO_DATE_TIME.format((ZonedDateTime)clonedInMemoryCache.get(Constants.TIMESTAMP));
            String xCacheVersion = (String)clonedInMemoryCache.getOrDefault(Constants.CACHE_VERSION, NA);

            // generate v1 cache version
            ConfigDataV1 configDataV1 = cacheToConfigDataV1(clonedInMemoryCache, keys);

            configData = ConfigData.builder()
                    .configDataV1(configDataV1)
                    .xCacheId(xCacheId)
                    .xCacheTimestamp(xCacheTimestamp)
                    .xCacheVersion(xCacheVersion)
                    .build();

            // save cache on redis
            String actualKey = getKeyV1(stakeholder);
            String actualKeyV1 = getKeyV1Id(stakeholder);

            byte[] cacheByteArray = compressJsonToGzip(configData);

            log.info(String.format("saving on Redis %s %s", actualKey, actualKeyV1));
            redisRepository.pushToRedisAsync(actualKey, actualKeyV1, cacheByteArray, configDataV1.getVersion().getBytes(StandardCharsets.UTF_8));
        }

        // TODO to test!
        if (saveDB || true) {
            ConfigDataV1 configDataV1 = configData.getConfigDataV1();
            log.info("saving on CACHE table " + configDataV1.getVersion());
            try {
//                HashMap<String, Object> cloned = (HashMap<String, Object>)configData.getConfigDataV1().clone();
                ObjectMapper objectMapper = new ObjectMapper();
                HashMap<String, Object> cloned = objectMapper.convertValue(configDataV1, HashMap.class);
//                cloned.remove(Constants.TIMESTAMP);
//                cloned.remove(Constants.CACHE_VERSION);
                //cloned to remove data not in ConfigDataV1
//                cacheRepository.save(
//                        Cache.builder()
//                                .id(id)
//                                .cache(jsonSerializer.serialize(cloned))
//                                .time(now)
//                                .version(getVersion())
//                                .build());
                log.info("saved on CACHE table " + "id");
            } catch (Exception e) {
                log.error("[ALERT] could not save on db", e);
            }
        }

        return configData;
    }

    public byte[] getXLSX(String stakeholder, String[] keys) throws IOException {
        ConfigData configData = getCache(stakeholder, keys);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> configDataV1 = objectMapper.convertValue(configData.getConfigDataV1(), HashMap.class);
        // TODO fix xlsx
        return new JsonToXls(xlsMaskPasswords).convert(configDataV1);
    }

    private ConfigDataV1 cacheToConfigDataV1(Map<String,Object> inMemoryCache, String[] keys) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Set<String> keysSet = new HashSet<>(Arrays.asList(keys));
        inMemoryCache.keySet().removeIf(key -> !keysSet.contains(key));
        return objectMapper.convertValue(inMemoryCache, ConfigDataV1.class);
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

    private String getKeyV1(String stakeholder) {
        return keyV1.replace(stakeholderPlaceholder, stakeholder) + keySuffix;
    }

    private String getKeyV1Id(String stakeholder) {
        return keyV1Id.replace(stakeholderPlaceholder, stakeholder) + keySuffix;
    }

    private String getKeyV1InProgress(String stakeholder) {
        return keyV1InProgress.replace(stakeholderPlaceholder, stakeholder) + keySuffix;
    }


}
