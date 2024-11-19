package it.gov.pagopa.apiconfig.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheKeyUtils;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import it.gov.pagopa.apiconfig.starter.repository.CanaliViewRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiDetailRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiFasciaCostoServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiInformazioniServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiMasterValidRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdiPreferenceRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdsCategorieRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdsServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdsSoggettoRepository;
import it.gov.pagopa.apiconfig.starter.repository.CdsSoggettoServizioRepository;
import it.gov.pagopa.apiconfig.starter.repository.CodifichePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.CodificheRepository;
import it.gov.pagopa.apiconfig.starter.repository.ConfigurationKeysRepository;
import it.gov.pagopa.apiconfig.starter.repository.DizionarioMetadatiRepository;
import it.gov.pagopa.apiconfig.starter.repository.FtpServersRepository;
import it.gov.pagopa.apiconfig.starter.repository.GdeConfigRepository;
import it.gov.pagopa.apiconfig.starter.repository.IbanValidiPerPaRepository;
import it.gov.pagopa.apiconfig.starter.repository.InformativePaDetailRepository;
import it.gov.pagopa.apiconfig.starter.repository.InformativePaFasceRepository;
import it.gov.pagopa.apiconfig.starter.repository.InformativePaMasterRepository;
import it.gov.pagopa.apiconfig.starter.repository.IntermediariPaRepository;
import it.gov.pagopa.apiconfig.starter.repository.IntermediariPspRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspCanaleTipoVersamentoCanaleRepository;
import it.gov.pagopa.apiconfig.starter.repository.PspRepository;
import it.gov.pagopa.apiconfig.starter.repository.StazioniRepository;
import it.gov.pagopa.apiconfig.starter.repository.TipiVersamentoRepository;
import it.gov.pagopa.apiconfig.starter.repository.WfespPluginConfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
  void loadFullCache() throws Exception {
    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    Map<String, Object> allData = cacheConfigService.loadFullCache();
    assertThat(allData).hasSize(28);
    assertThat(allData.get("version")).isEqualTo("testversion");
  }

  @Test
  void newCache() throws Exception {
    ReflectionTestUtils.setField(cacheConfigService, "cacheKeyUtils", cacheKeyUtils);
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    ReflectionTestUtils.setField(cacheConfigService, "modelMapper", modelMapper);

    Map<String, Object> allData = cacheConfigService.newCache();
    assertThat(allData).hasSize(28);
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


}
