package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.controller.stakeholders.NodeCacheController;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.CacheKeyUtils;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import it.gov.pagopa.apiconfig.cache.util.FileDeleter;
import it.gov.pagopa.apiconfig.cache.util.JsonSerializer;
import it.gov.pagopa.apiconfig.starter.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StakeholderConfigServiceTest {

  @Mock private CacheRepository cacheRepository;
  @Mock private RedisRepository redisRepository;
  @Mock private ConfigurationKeysRepository configurationKeysRepository;
  @Mock private IntermediariPaRepository intermediariPaRepository;
  @Mock private IntermediariPspRepository intermediariPspRepository;
  @Mock private CdsCategorieRepository cdsCategorieRepository;
  @Mock private CdsSoggettoRepository cdsSoggettoRepository;
  @Mock private CdsServizioRepository cdsServizioRepository;
  @Mock private CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  @Mock private GdeConfigRepository gdeConfigRepository;
  @Mock private DizionarioMetadatiRepository dizionarioMetadatiRepository;
  @Mock private FtpServersRepository ftpServersRepository;
  @Mock private TipiVersamentoRepository tipiVersamentoRepository;
  @Mock private WfespPluginConfRepository wfespPluginConfRepository;
  @Mock private CodifichePaRepository codifichePaRepository;
  @Mock private CodificheRepository codificheRepository;
  @Mock private IbanValidiPerPaRepository ibanValidiPerPaRepository;
  @Mock private StazioniRepository stazioniRepository;
  @Mock private PaStazionePaRepository paStazioniRepository;
  @Mock private PaRepository paRepository;
  @Mock private CanaliViewRepository canaliRepository;
  @Mock private PspCanaleTipoVersamentoCanaleRepository pspCanaleTipoVersamentoCanaleRepository;
  @Mock private PspRepository pspRepository;
  @Mock private CdiMasterValidRepository cdiMasterValidRepository;
  @Mock private CdiDetailRepository cdiDetailRepository;
  @Mock private CdiPreferenceRepository cdiPreferenceRepository;
  @Mock private CdiInformazioniServizioRepository cdiInformazioniServizioRepository;
  @Mock private CdiFasciaCostoServizioRepository cdiFasceRepository;
  @Mock private InformativePaMasterRepository informativePaMasterRepository;
  @Mock private InformativePaDetailRepository informativePaDetailRepository;
  @Mock private InformativePaFasceRepository informativePaFasceRepository;
  @Mock private JsonSerializer jsonSerializer;
  @Mock private CacheEventHubService cacheEventHubService;

  @Spy private ConfigMapper configMapper = new ConfigMapper();

  @InjectMocks private CacheConfigService cacheConfigService;

  @InjectMocks private StakeholderConfigService stakeholderConfigService;
  private CacheController cacheController;
  private CacheKeyUtils cacheKeyUtils;

  @BeforeEach
  void setUp() {
    cacheKeyUtils = new CacheKeyUtils();
    cacheController = new CacheController();

    // Set @Value fields manually
    ReflectionTestUtils.setField(cacheKeyUtils, "KEY_SUFFIX", "_test");
    ReflectionTestUtils.setField(cacheKeyUtils, "CACHE_KEY_IN_PROGRESS", "apicfg_test_{{stakeholder}}_in_progress");
    ReflectionTestUtils.setField(cacheKeyUtils, "CACHE_KEY", "apicfg_test_{{stakeholder}}");
    ReflectionTestUtils.setField(cacheKeyUtils, "CACHE_ID_KEY", "apicfg_test_{{stakeholder}}_id");
    org.springframework.test.util.ReflectionTestUtils.setField(stakeholderConfigService, "cacheKeyUtils", cacheKeyUtils);
    org.springframework.test.util.ReflectionTestUtils.setField(cacheController, "cacheConfigService", cacheConfigService);
    org.springframework.test.util.ReflectionTestUtils.setField(stakeholderConfigService, "cacheController", cacheController);
  }

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
    ConfigData configData = stakeholderConfigService.getCache(Stakeholder.TEST, "v1", NodeCacheController.KEYS);
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
    ConfigData configData = stakeholderConfigService.getCache(Stakeholder.STANDIN, "v1", NodeCacheController.KEYS);
    assertThat(configData).isNotNull();
  }

  @Test
  void saveOnDB() throws IOException {
    when(redisRepository.get(any())).thenReturn(null);
    when(cacheRepository.save(any())).thenReturn(null);
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
    TestUtils.inizializeInMemoryCache(cacheController, configMapper, version, cacheVersion, romeDateTime);
    ConfigData configData = stakeholderConfigService.getCache(Stakeholder.TEST, "v1", NodeCacheController.KEYS);
    stakeholderConfigService.saveOnDB(configData, "v1");
    assertThat(configData).isNotNull();
  }
  @Test
  void getVersionId() throws IOException {
    String version = "111";
    when(redisRepository.get(any())).thenReturn(version.getBytes(StandardCharsets.UTF_8));
    CacheVersion cacheVersion = stakeholderConfigService.getVersionId(Stakeholder.TEST, "v1", NodeCacheController.KEYS);
    assertThat(cacheVersion.getVersion()).isEqualTo(version);
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
          byte[] result = StakeholderConfigService.compressJsonToGzipFile(configData);
          assertThat(result).isNotEmpty();
      });

      verify(failingDeleter).delete(any(Path.class));
  }






}
