package it.gov.pagopa.apiconfig.cache.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.TestUtils;
import it.gov.pagopa.apiconfig.cache.config.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.config.ObjectMapperConfig;
import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import it.gov.pagopa.apiconfig.starter.repository.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(
    classes = {
      StakeholderConfigService.class,
      ConfigMapper.class,
      CacheKeyUtils.class,
      CacheController.class,
      ObjectMapperConfig.class
    })
class StakeholderConfigServiceTest {

  public static final String[] KEYS = {Constants.CHANNELS, Constants.STATIONS};
  public static final String CACHE_VERSION = "id";

  @MockBean private CacheRepository cacheRepository;
  @MockBean private RedisRepository redisRepository;
  @MockBean private CacheConfigService cacheConfigService;

  @Autowired private ModelMapper configMapper;
  @Autowired private CacheKeyUtils cacheKeyUtils;
  @Autowired private ObjectMapper objectMapper;

  @Autowired private StakeholderConfigService sut;

  @Test
  void getStakeholderCacheVersionFromRedisIfUpToDate_ok() {
    String stakeholderWithSchema = sut.getStakeholderWithSchema(Stakeholder.TEST, "v1");
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholderWithSchema)))
            .thenReturn(CACHE_VERSION.getBytes(StandardCharsets.UTF_8));
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL)))
            .thenReturn(CACHE_VERSION.getBytes(StandardCharsets.UTF_8));

    String cacheVersion = assertDoesNotThrow(() ->
            sut.getStakeholderCacheVersionFromRedisIfUpToDate(Stakeholder.TEST, "v1"));

    assertNotNull(cacheVersion);
    assertEquals(CACHE_VERSION, cacheVersion);
  }

  @Test
  void getStakeholderCacheVersionFromRedisIfUpToDate_cacheFullIdNull() {
    String stakeholderWithSchema = sut.getStakeholderWithSchema(Stakeholder.TEST, "v1");
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholderWithSchema)))
            .thenReturn(CACHE_VERSION.getBytes(StandardCharsets.UTF_8));
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL)))
            .thenReturn(null);

    String cacheVersion = assertDoesNotThrow(() ->
            sut.getStakeholderCacheVersionFromRedisIfUpToDate(Stakeholder.TEST, "v1"));

    assertNull(cacheVersion);
  }

  @Test
  void getStakeholderCacheVersionFromRedisIfUpToDate_cacheStakeholderIdNull() {
    String stakeholderWithSchema = sut.getStakeholderWithSchema(Stakeholder.TEST, "v1");
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholderWithSchema)))
            .thenReturn(null);
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL)))
            .thenReturn(CACHE_VERSION.getBytes(StandardCharsets.UTF_8));

    String cacheVersion = assertDoesNotThrow(() ->
            sut.getStakeholderCacheVersionFromRedisIfUpToDate(Stakeholder.TEST, "v1"));

    assertNull(cacheVersion);
  }

  @Test
  void getCache_cacheInitialized_ok() throws IOException {
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
    String stakeholderWithSchema = sut.getStakeholderWithSchema(Stakeholder.TEST, "v1");

    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholderWithSchema)))
            .thenReturn(CACHE_VERSION.getBytes(StandardCharsets.UTF_8));
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL)))
            .thenReturn(CACHE_VERSION.getBytes(StandardCharsets.UTF_8));

    HashMap<String, Object> cache = TestUtils.inMemoryCache(configMapper, CACHE_VERSION, cacheVersion, romeDateTime);
    when(redisRepository.get(cacheKeyUtils.getCacheKey(stakeholderWithSchema)))
            .thenReturn(ZipUtils.gzip(out -> objectMapper.writeValue(out, cache)));

    ConfigData configData = assertDoesNotThrow(() ->
            sut.getCache(Stakeholder.TEST, "v1", KEYS));

    assertNotNull(configData);
  }

  @Test
  void getCache_cacheNotInitialized_refresh() throws IOException {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);

    when(redisRepository.get(any())).thenReturn(null);
    doReturn(TestUtils.inMemoryCache(configMapper, version, cacheVersion, romeDateTime))
        .when(cacheConfigService)
        .getInMemoryCacheOrRefreshIfNotExist();

    ConfigData configData = sut.getCache(Stakeholder.TEST, "v1", KEYS);

    assertThat(configData).isNotNull();
  }

  @Test
  void getCache_standin() throws IOException {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);

    when(redisRepository.get(any())).thenReturn(null);
    doReturn(TestUtils.inMemoryCache(configMapper, version, cacheVersion, romeDateTime))
        .when(cacheConfigService)
        .getInMemoryCacheOrRefreshIfNotExist();

    ConfigData configData = sut.getCache(Stakeholder.STANDIN, "v1", KEYS);

    assertThat(configData).isNotNull();
  }

  @Test
  void getCache_concurrentRequests_generateOnlyOnce() throws Exception {
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(ZonedDateTime.now());
    HashMap<String, Object> cache =
        TestUtils.inMemoryCache(configMapper, CACHE_VERSION, cacheVersion, romeDateTime);
    String stakeholderWithSchema = sut.getStakeholderWithSchema(Stakeholder.TEST, "v1");
    byte[] id = CACHE_VERSION.getBytes(StandardCharsets.UTF_8);
    AtomicBoolean generated = new AtomicBoolean(false);

    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(Constants.FULL))).thenReturn(id);
    when(redisRepository.get(cacheKeyUtils.getCacheIdKey(stakeholderWithSchema)))
        .thenAnswer(i -> generated.get() ? id : null);
    when(redisRepository.get(cacheKeyUtils.getCacheKey(stakeholderWithSchema)))
        .thenReturn(ZipUtils.gzip(out -> objectMapper.writeValue(out, cache)));
    doReturn(cache).when(cacheConfigService).getInMemoryCacheOrRefreshIfNotExist();
    doAnswer(i -> {
          Thread.sleep(300);
          generated.set(true);
          return null;
        })
        .when(redisRepository)
        .pushToRedisSync(anyString(), anyString(), any(), any());

    ExecutorService executor = Executors.newFixedThreadPool(5);
    List<Future<ConfigData>> results = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      results.add(executor.submit(() -> sut.getCache(Stakeholder.TEST, "v1", KEYS)));
    }
    for (Future<ConfigData> result : results) {
      assertNotNull(result.get(10, TimeUnit.SECONDS));
    }
    executor.shutdown();

    verify(redisRepository, times(1)).pushToRedisSync(anyString(), anyString(), any(), any());
  }
  @Test
  void getVersionId() throws IOException {
    String version = "111";
    when(redisRepository.get(any())).thenReturn(version.getBytes(StandardCharsets.UTF_8));

    CacheVersion cacheVersion = sut.getVersionId(Stakeholder.TEST, "v1", KEYS);

    assertThat(cacheVersion.getVersion()).isEqualTo(version);
  }

  @Test
  void getVersionId_regenerate() throws IOException {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);

    doReturn(TestUtils.inMemoryCache(configMapper, version, cacheVersion, romeDateTime))
        .when(cacheConfigService)
        .getInMemoryCacheOrRefreshIfNotExist();
    when(redisRepository.get(any())).thenReturn(null);

    CacheVersion generatedCache =
        sut.getVersionId(Stakeholder.TEST, "v1", KEYS);

    assertThat(generatedCache.getVersion()).isEqualTo(version);
  }

  @Test
  void getXLSX() throws IOException {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
    ConfigDataV1 configDataV1 =
        sut.cacheToConfigDataV1(
            Stakeholder.TEST,
            TestUtils.inMemoryCache(configMapper, version, cacheVersion, romeDateTime));
    ConfigData configData =
        ConfigData.builder()
            .cacheSchemaVersion(configDataV1)
            .xCacheId(version)
            .xCacheTimestamp(romeDateTime.toString())
            .xCacheVersion(cacheVersion)
            .build();

    when(redisRepository.get(any()))
        .thenReturn(sut.compressJsonToGzip(configData));

    assertThat(sut.getXLSX(Stakeholder.TEST, "v1")).isNotNull();
  }

  @Test
  void testCompressJsonToGzip() throws IOException {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
    ConfigDataV1 configDataV1 =
        sut.cacheToConfigDataV1(
            Stakeholder.TEST,
            TestUtils.inMemoryCache(configMapper, version, cacheVersion, romeDateTime));
    ConfigData configData =
        ConfigData.builder()
            .cacheSchemaVersion(configDataV1)
            .xCacheId(version)
            .xCacheTimestamp(romeDateTime.toString())
            .xCacheVersion(cacheVersion)
            .build();

    byte[] compressed = sut.compressJsonToGzip(configData);

    assertThat(compressed).isNotNull().isNotEmpty();
  }
}
