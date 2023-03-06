package it.gov.pagopa.apiconfig;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.redis.RedisRepository;
import it.gov.pagopa.apiconfig.service.ConfigService;
import it.gov.pagopa.apiconfig.starter.repository.CanaliRepository;
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
import it.gov.pagopa.apiconfig.util.ConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;

//@SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class NodoConfigCacheTest {

  @Mock
  private PlatformTransactionManager transactionManager;
  @Mock
  private RedisRepository redisRepository;
  @Mock
  private ConfigurationKeysRepository configurationKeysRepository;
  @Mock
  private IntermediariPaRepository intermediariPaRepository;
  @Mock
  private IntermediariPspRepository intermediariPspRepository;
  @Mock
  private CdsCategorieRepository cdsCategorieRepository;
  @Mock
  private CdsSoggettoRepository cdsSoggettoRepository;
  @Mock
  private CdsServizioRepository cdsServizioRepository;
  @Mock
  private CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  @Mock
  private GdeConfigRepository gdeConfigRepository;
  @Mock
  private DizionarioMetadatiRepository dizionarioMetadatiRepository;
  @Mock
  private FtpServersRepository ftpServersRepository;
  @Mock
  private TipiVersamentoRepository tipiVersamentoRepository;
  @Mock
  private WfespPluginConfRepository wfespPluginConfRepository;
  @Mock
  private CodifichePaRepository codifichePaRepository;
  @Mock
  private CodificheRepository codificheRepository;
  @Mock
  private IbanValidiPerPaRepository ibanValidiPerPaRepository;
  @Mock
  private StazioniRepository stazioniRepository;
  @Mock
  private PaStazionePaRepository paStazioniRepository;
  @Mock
  private PaRepository paRepository;
  @Mock
  private CanaliRepository canaliRepository;
  @Mock
  private PspCanaleTipoVersamentoCanaleRepository pspCanaleTipoVersamentoCanaleRepository;
  @Mock
  private PspRepository pspRepository;
  @Mock
  private CdiMasterValidRepository cdiMasterValidRepository;
  @Mock
  private CdiDetailRepository cdiDetailRepository;
  @Mock
  private CdiPreferenceRepository cdiPreferenceRepository;
  @Mock
  private CdiInformazioniServizioRepository cdiInformazioniServizioRepository;
  @Mock
  private CdiFasciaCostoServizioRepository cdiFasceRepository;
  @Mock
  private InformativePaMasterRepository informativePaMasterRepository;
  @Mock
  private InformativePaDetailRepository informativePaDetailRepository;
  @Mock
  private InformativePaFasceRepository informativePaFasceRepository;

  @Spy
  private ConfigMapper configMapper = new ConfigMapper();

  @InjectMocks
  private ConfigService configService;

  @BeforeEach
  void setUp() {
    when(configurationKeysRepository.findAll()).thenReturn(TestUtils.mockConfigurationKeys);
    when(dizionarioMetadatiRepository.findAll()).thenReturn(TestUtils.mockMetadataDicts);
    when(paRepository.findAll()).thenReturn(TestUtils.pas);
    when(pspRepository.findAll()).thenReturn(TestUtils.psps);
  }

  @Test
  void getCacheV1() throws Exception {
    ConfigDataV1 allData = configService.newCacheV1();
    assertThat(allData.getConfigurations()).containsKey(
        TestUtils.mockConfigurationKeys.get(0).getConfigCategory()+"-"+
            TestUtils.mockConfigurationKeys.get(0).getConfigKey());
    assertThat(allData.getMetadataDict()).containsKey(TestUtils.mockMetadataDicts.get(0).getKey());
    assertThat(allData.getCreditorInstitutions()).containsKey(TestUtils.pas.get(0).getIdDominio());
    assertThat(allData.getCreditorInstitutionInformations()).containsKey(TestUtils.pas.get(0).getIdDominio());
    assertThat(allData.getPsps()).containsKey(TestUtils.psps.get(0).getIdPsp());
    assertThat(allData.getPspInformationTemplates()).containsKey(TestUtils.psps.get(0).getIdPsp());
  }

}
