package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheKeyUtils;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import it.gov.pagopa.apiconfig.cache.util.FileDeleter;
import it.gov.pagopa.apiconfig.starter.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {StakeholderConfigService.class, ConfigMapper.class, CacheKeyUtils.class, CacheController.class})
class StakeholderConfigServiceTest {

  public static final String[] KEYS = {Constants.CHANNELS, Constants.STATIONS};

  @MockBean
  private CacheRepository cacheRepository;
  @MockBean private RedisRepository redisRepository;
  @MockBean private CacheConfigService cacheConfigService;

  @Autowired
  private CacheController cacheController;
  @Autowired
  private ConfigMapper configMapper;

  @Autowired
  private StakeholderConfigService stakeholderConfigService;

  @Test
  void loadCache() throws IOException {
    when(redisRepository.get(any())).thenReturn(null);
    ConfigData configData = stakeholderConfigService.loadCache(Stakeholder.TEST, "v1");
    assertThat(configData).isNull();
  }
  @Test
  void getCache() throws IOException {
    when(redisRepository.get(any())).thenReturn(null);
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
    TestUtils.inizializeInMemoryCache(cacheController, configMapper, version, cacheVersion, romeDateTime);
    when(redisRepository.saveIfAbsent(anyString(), any(), anyLong())).thenReturn(true);
    ConfigData configData = stakeholderConfigService.getCache(Stakeholder.TEST, "v1", KEYS);
    assertThat(configData).isNotNull();
  }

  @Test
  void getCache_standin() throws IOException {
    when(redisRepository.get(any())).thenReturn(null);
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
    TestUtils.inizializeInMemoryCache(cacheController, configMapper, version, cacheVersion, romeDateTime);
    when(redisRepository.saveIfAbsent(anyString(), any(), anyLong())).thenReturn(true);
    ConfigData configData = stakeholderConfigService.getCache(Stakeholder.STANDIN, "v1", KEYS);
    assertThat(configData).isNotNull();
  }

  @Test
  void getVersionId() throws IOException {
      String version = "111";
      when(redisRepository.get(any())).thenReturn(version.getBytes(StandardCharsets.UTF_8));
      CacheVersion cacheVersion = stakeholderConfigService.getVersionId(Stakeholder.TEST, "v1", KEYS);
      assertThat(cacheVersion.getVersion()).isEqualTo(version);
  }

  @Test
  void getVersionId_regenerate() throws IOException {
      String version = "111";
      String cacheVersion = Constants.GZIP_JSON + "-test";
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
      TestUtils.inizializeInMemoryCache(cacheController, configMapper, version, cacheVersion, romeDateTime);
      when(redisRepository.get(any())).thenReturn(null);
      when(redisRepository.saveIfAbsent(anyString(), any(), anyLong())).thenReturn(true);
      CacheVersion generatedCache = stakeholderConfigService.getVersionId(Stakeholder.TEST, "v1", KEYS);
      assertThat(generatedCache.getVersion()).isEqualTo(version);
  }


  @Test
  void getXLSX() throws IOException {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
    ConfigMapper modelMapper = new ConfigMapper();
    ConfigDataV1 configDataV1 = stakeholderConfigService.cacheToConfigDataV1(
            Stakeholder.TEST,
            TestUtils.inMemoryCache(
                    modelMapper, version, cacheVersion, romeDateTime
            ),
            new String[]{}
    );
    ConfigData configData = ConfigData.builder()
            .cacheSchemaVersion(configDataV1)
            .xCacheId(version)
            .xCacheTimestamp(romeDateTime.toString())
            .xCacheVersion(cacheVersion)
            .build();

    when(redisRepository.get(any())).thenReturn(stakeholderConfigService.compressJsonToGzip(configData));

    assertThat(stakeholderConfigService.getXLSX(Stakeholder.TEST, "v1")).isNotNull();
  }
  
  @Test
  void testCompressJsonToGzipFile() throws IOException {
	  String version = "111";
	  String cacheVersion = Constants.GZIP_JSON + "-test";
	  ZonedDateTime now = ZonedDateTime.now();
	  ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
	  ConfigMapper modelMapper = new ConfigMapper();
	  ConfigDataV1 configDataV1 = stakeholderConfigService.cacheToConfigDataV1(
			  Stakeholder.TEST,
			  TestUtils.inMemoryCache(
					  modelMapper, version, cacheVersion, romeDateTime
					  ),
			  new String[]{}
			  );
	  ConfigData configData = ConfigData.builder()
			  .cacheSchemaVersion(configDataV1)
			  .xCacheId(version)
			  .xCacheTimestamp(romeDateTime.toString())
			  .xCacheVersion(cacheVersion)
			  .build();

	  byte[] compressed = StakeholderConfigService.compressJsonToGzipFile(configData);

	  assertThat(compressed).isNotNull().isNotEmpty();
  }
  
  @Test
  void testCompressJsonToGzipFile_DeleteFails() throws IOException {
      String version = "111";
      String cacheVersion = Constants.GZIP_JSON + "-test";
      ZonedDateTime now = ZonedDateTime.now();
      ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
      ConfigMapper modelMapper = new ConfigMapper();

      ConfigDataV1 configDataV1 = stakeholderConfigService.cacheToConfigDataV1(
          Stakeholder.TEST,
          TestUtils.inMemoryCache(modelMapper, version, cacheVersion, romeDateTime),
          new String[]{}
      );

      ConfigData configData = ConfigData.builder()
          .cacheSchemaVersion(configDataV1)
          .xCacheId(version)
          .xCacheTimestamp(romeDateTime.toString())
          .xCacheVersion(cacheVersion)
          .build();

      FileDeleter failingDeleter = mock(FileDeleter.class);
      doThrow(new IOException("Simulated delete failure")).when(failingDeleter).delete(any(Path.class));

      assertDoesNotThrow(() -> {
          byte[] result = StakeholderConfigService.compressJsonToGzipFile(configData, failingDeleter);
          assertThat(result).isNotEmpty();
      });

      verify(failingDeleter).delete(any(Path.class));
  }






}
