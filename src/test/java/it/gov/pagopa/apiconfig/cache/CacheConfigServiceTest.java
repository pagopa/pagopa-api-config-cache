package it.gov.pagopa.apiconfig.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.CacheKeyUtils;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import it.gov.pagopa.apiconfig.starter.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// @SpringBootTest(classes = Application.class)
@ExtendWith({MockitoExtension.class})
@MockitoSettings(strictness = Strictness.LENIENT)
class CacheConfigServiceTest {

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
  @Mock private StationMaintenanceRepository stationMaintenanceRepository;
  @Mock private PaRepository paRepository;
  @Mock private CanaliViewRepository canaliViewRepository;
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
  @Mock private CacheEventHubService eventHubService;

  @InjectMocks private CacheConfigService cacheConfigService;

  private ConfigMapper modelMapper;

  private CacheKeyUtils cacheKeyUtils;

  @BeforeEach
  void setUp() throws IOException {
    modelMapper = new ConfigMapper();
    cacheKeyUtils = new CacheKeyUtils();

    // Set @Value fields manually
    ReflectionTestUtils.setField(cacheKeyUtils, "KEY_SUFFIX", "_test");
    ReflectionTestUtils.setField(cacheKeyUtils, "CACHE_KEY_IN_PROGRESS", "apicfg_test_{{stakeholder}}_in_progress");
    ReflectionTestUtils.setField(cacheKeyUtils, "CACHE_KEY", "apicfg_test_{{stakeholder}}");
    ReflectionTestUtils.setField(cacheKeyUtils, "CACHE_ID_KEY", "apicfg_test_{{stakeholder}}_id");
    when(redisRepository.get(any())).thenReturn(ZipUtils.zip("{\"version\": \"testversion\"}".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void testPostConstruct() {
    CacheConfigService service = new CacheConfigService();
    service.postConstruct(); // manually invoke

    assertDoesNotThrow(service::postConstruct, "postConstruct() should execute without exception");
  }

  @Test
  void loadFullCache() throws Exception {
    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    Map<String, Object> allData = cacheConfigService.loadFullCache();
    assertThat(allData).hasSize(29);
    assertThat(allData.get("version")).isEqualTo("testversion");
  }

  @Test
  void newCache() throws Exception {
    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    ReflectionTestUtils.setField(cacheConfigService, "modelMapper", modelMapper);

    Map<String, Object> allData = cacheConfigService.newCache();
    assertThat(allData).hasSize(29);
  }

  @Test
  void newCacheException() {
    doThrow(new RuntimeException()).when(intermediariPaRepository).findAll();

    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    ReflectionTestUtils.setField(cacheConfigService, "modelMapper", modelMapper);

    assertThrows(RuntimeException.class, () -> cacheConfigService.newCache());
  }

  @Test
  void getCacheInProgress() {
    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    ReflectionTestUtils.setField(cacheConfigService, "modelMapper", modelMapper);
    when(redisRepository.getBooleanByKeyId(any())).thenReturn(true);
    cacheConfigService.getCacheInProgress();
    assertThat(cacheConfigService.getCacheInProgress()).isTrue();
  }

  @Test
  void getCacheId_1() {
    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    ReflectionTestUtils.setField(cacheConfigService, "modelMapper", modelMapper);
    when(redisRepository.getStringByKeyId(any())).thenReturn("version");
    assertThat(cacheConfigService.getCacheId()).isNotNull();
  }

  @Test
  void getCacheId_2() {
    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    ReflectionTestUtils.setField(cacheConfigService, "modelMapper", modelMapper);
    when(redisRepository.getStringByKeyId(any())).thenReturn(null);
    assertThrows(AppException.class, () -> cacheConfigService.getCacheId());
  }

  @Test
  void testSendEvent_publishEventCalled() throws JsonProcessingException {
    ReflectionTestUtils.setField(cacheConfigService, "SEND_EVENT", true);
    doNothing().when(eventHubService).publishEvent(anyString(), any(ZonedDateTime.class), anyString());

    ZonedDateTime now = ZonedDateTime.now();
    cacheConfigService.sendEvent("ID-1", now);

    verify(eventHubService, times(1))
            .publishEvent(eq("ID-1"), eq(now), anyString());
  }

  @Test
  void testSendEvent_publishEventThrowsJsonProcessingException() throws JsonProcessingException {
    ReflectionTestUtils.setField(cacheConfigService, "SEND_EVENT", true);
    doThrow(JsonProcessingException.class).when(eventHubService).publishEvent(anyString(), any(ZonedDateTime.class), anyString());

    ZonedDateTime now = ZonedDateTime.now();
    AppException thrown = assertThrows(AppException.class, () -> {
      cacheConfigService.sendEvent("ID-1", now);
    });

    assertInstanceOf(JsonProcessingException.class, thrown.getCause());
  }
}
