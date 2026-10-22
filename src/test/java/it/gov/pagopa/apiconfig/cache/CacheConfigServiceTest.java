package it.gov.pagopa.apiconfig.cache;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.config.MarshalerConfig;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.CacheKeyUtils;
import it.gov.pagopa.apiconfig.cache.config.ConfigMapper;
import it.gov.pagopa.apiconfig.starter.repository.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(
    classes = {
      CacheConfigService.class,
      ConfigMapper.class,
      MarshalerConfig.class,
      CacheKeyUtils.class,
      ObjectMapper.class
    })
@MockitoSettings(strictness = Strictness.LENIENT)
class CacheConfigServiceTest {

  @MockBean private RedisRepository redisRepository;
  @MockBean private ConfigurationKeysRepository configurationKeysRepository;
  @MockBean private IntermediariPaRepository intermediariPaRepository;
  @MockBean private IntermediariPspRepository intermediariPspRepository;
  @MockBean private CdsCategorieRepository cdsCategorieRepository;
  @MockBean private CdsSoggettoRepository cdsSoggettoRepository;
  @MockBean private CdsServizioRepository cdsServizioRepository;
  @MockBean private CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  @MockBean private GdeConfigRepository gdeConfigRepository;
  @MockBean private DizionarioMetadatiRepository dizionarioMetadatiRepository;
  @MockBean private FtpServersRepository ftpServersRepository;
  @MockBean private TipiVersamentoRepository tipiVersamentoRepository;
  @MockBean private WfespPluginConfRepository wfespPluginConfRepository;
  @MockBean private CodifichePaRepository codifichePaRepository;
  @MockBean private CodificheRepository codificheRepository;
  @MockBean private IbanValidiPerPaRepository ibanValidiPerPaRepository;
  @MockBean private StazioniRepository stazioniRepository;
  @MockBean private PaStazionePaRepository paStazioniRepository;
  @MockBean private StationMaintenanceRepository stationMaintenanceRepository;
  @MockBean private PaRepository paRepository;
  @MockBean private CanaliViewRepository canaliViewRepository;
  @MockBean private PspCanaleTipoVersamentoCanaleRepository pspCanaleTipoVersamentoCanaleRepository;
  @MockBean private PspRepository pspRepository;
  @MockBean private CdiMasterValidRepository cdiMasterValidRepository;
  @MockBean private CdiPreferenceRepository cdiPreferenceRepository;
  @MockBean private InformativePaMasterRepository informativePaMasterRepository;
  @MockBean private InformativePaFasceRepository informativePaFasceRepository;
  @MockBean private CacheEventHubService eventHubService;

  @Autowired private CacheConfigService cacheConfigService;

  @BeforeEach
  void setUp() throws IOException {
    when(redisRepository.get(any()))
        .thenReturn(zip("{\"version\": \"testversion\"}".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void loadFullCache() throws Exception {
    Map<String, Object> allData = cacheConfigService.loadFullCache();
    assertThat(allData).hasSize(29);
    assertThat(allData).containsEntry("version", "testversion");
  }

  @Test
  void newCache() {
    Map<String, Object> allData = cacheConfigService.newCache();
    assertThat(allData).hasSize(29);
  }

  @Test
  void newCacheException() {
    doThrow(new RuntimeException()).when(intermediariPaRepository).findAll();

    assertThrows(RuntimeException.class, () -> cacheConfigService.newCache());
  }

  @Test
  void getCacheInProgress() {
    when(redisRepository.getBooleanByKeyId(any())).thenReturn(true);
    cacheConfigService.getCacheInProgress();
    assertThat(cacheConfigService.getCacheInProgress()).isTrue();
  }

  @Test
  void getCacheId_1() {
    when(redisRepository.getStringByKeyId(any())).thenReturn("version");
    assertThat(cacheConfigService.getCacheId()).isNotNull();
  }

  @Test
  void getCacheId_2() {
    when(redisRepository.getStringByKeyId(any())).thenReturn(null);
    assertThrows(AppException.class, () -> cacheConfigService.getCacheId());
  }

  @Test
  void testSendEvent_publishEventCalled() throws JsonProcessingException {
    doNothing()
        .when(eventHubService)
        .publishEvent(anyString(), any(ZonedDateTime.class), anyString());

    ZonedDateTime now = ZonedDateTime.now();
    cacheConfigService.sendEvent("ID-1", now);

    verify(eventHubService, times(1)).publishEvent(eq("ID-1"), eq(now), anyString());
  }

  @Test
  void testSendEvent_publishEventThrowsJsonProcessingException() throws JsonProcessingException {
    doThrow(JsonProcessingException.class)
        .when(eventHubService)
        .publishEvent(anyString(), any(ZonedDateTime.class), anyString());

    ZonedDateTime now = ZonedDateTime.now();
    AppException thrown =
        assertThrows(
            AppException.class,
            () -> {
              cacheConfigService.sendEvent("ID-1", now);
            });

    assertInstanceOf(JsonProcessingException.class, thrown.getCause());
  }

  private byte[] zip(byte[] bytes) throws IOException {
    if (bytes == null) {
      return new byte[0];
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
      gzipOut.write(bytes);
    }
    return baos.toByteArray();
  }
}
