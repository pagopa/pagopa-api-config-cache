package it.gov.pagopa.apiconfig.cache;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class NodoConfigCacheTest {

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

  @Spy private ConfigMapper configMapper = new ConfigMapper();

  @InjectMocks private ConfigService configService;

  @BeforeEach
  void setUp() {
    org.springframework.test.util.ReflectionTestUtils.setField(configService, "keyV1Id", "value");
    org.springframework.test.util.ReflectionTestUtils.setField(configService, "keyV1", "value");
    org.springframework.test.util.ReflectionTestUtils.setField(
        configService, "keyV1InProgress", "value");
  }

  @Test
  void getCacheV1Id() {
    when(redisRepository.getStringByKeyId(anyString())).thenReturn(TestUtils.cacheId);
    CacheVersion cacheV1Id = configService.getCacheV1Id("");
    assertThat(cacheV1Id.getVersion().equals(TestUtils.cacheId));
  }

  @Test
  void getCacheV1() throws Exception {
    when(configurationKeysRepository.findAll()).thenReturn(TestUtils.mockConfigurationKeys);
    when(dizionarioMetadatiRepository.findAll()).thenReturn(TestUtils.mockMetadataDicts);
    when(paRepository.findAll()).thenReturn(TestUtils.pas);
    when(pspRepository.findAll()).thenReturn(TestUtils.psps);
    when(intermediariPaRepository.findAll()).thenReturn(TestUtils.intpas);
    when(intermediariPspRepository.findAll()).thenReturn(TestUtils.intpsp);
    when(cdiMasterValidRepository.findAll()).thenReturn(TestUtils.cdiMasterValid);
    when(cdiDetailRepository.findAll()).thenReturn(TestUtils.cdiDetail);
    when(cdiPreferenceRepository.findAll()).thenReturn(TestUtils.cdiPreference);
    when(cdiFasceRepository.findAll()).thenReturn(TestUtils.cdiFasciaCostoServizio);
    when(cdiInformazioniServizioRepository.findAll()).thenReturn(TestUtils.cdiInformazioniServizio);
    when(canaliRepository.findAllFetchingIntermediario()).thenReturn(TestUtils.canali);
    when(tipiVersamentoRepository.findAll()).thenReturn(TestUtils.tipiVersamento);
    when(pspCanaleTipoVersamentoCanaleRepository.findAllFetching())
        .thenReturn(TestUtils.pspCanaliTv);
    when(ftpServersRepository.findAll()).thenReturn(TestUtils.ftpServers);
    when(gdeConfigRepository.findAll()).thenReturn(TestUtils.gdeConfigurations);
    when(wfespPluginConfRepository.findAll()).thenReturn(TestUtils.plugins);
    when(ibanValidiPerPaRepository.findAllFetchingPas()).thenReturn(TestUtils.ibans);
    when(codifichePaRepository.findAllFetchingCodifiche()).thenReturn(TestUtils.encodingsPA);
    when(codificheRepository.findAll()).thenReturn(TestUtils.encodings);
    when(stazioniRepository.findAllFetchingIntermediario()).thenReturn(TestUtils.stazioni);
    when(paStazioniRepository.findAllFetching()).thenReturn(TestUtils.paStazioniPa);
    when(cdsServizioRepository.findAllFetching()).thenReturn(TestUtils.cdsServizi);
    when(cdsSoggettoServizioRepository.findAllFetchingStations())
        .thenReturn(TestUtils.cdsSoggettiServizi);
    when(cdsSoggettoRepository.findAll()).thenReturn(TestUtils.cdsSoggetti);
    when(cdsCategorieRepository.findAll()).thenReturn(TestUtils.cdsCategorie);

    ConfigDataV1 allData = configService.newCacheV1("node", Optional.empty());
    assertThat(allData.getConfigurations())
        .containsKey(
            TestUtils.mockConfigurationKeys.get(0).getConfigCategory()
                + "-"
                + TestUtils.mockConfigurationKeys.get(0).getConfigKey())
        .containsKey(
            TestUtils.mockConfigurationKeys.get(1).getConfigCategory()
                + "-"
                + TestUtils.mockConfigurationKeys.get(1).getConfigKey());
    assertThat(allData.getMetadataDict())
        .containsKey(TestUtils.mockMetadataDicts.get(0).getKey())
        .containsKey(TestUtils.mockMetadataDicts.get(1).getKey());
    assertThat(allData.getCreditorInstitutions())
        .containsKey(TestUtils.pas.get(0).getIdDominio())
        .containsKey(TestUtils.pas.get(1).getIdDominio());
    assertThat(allData.getCreditorInstitutionInformations())
        .containsKey(TestUtils.pas.get(0).getIdDominio())
        .containsKey(TestUtils.pas.get(1).getIdDominio());
    assertThat(allData.getCreditorInstitutionBrokers())
        .containsKey(TestUtils.intpas.get(0).getIdIntermediarioPa())
        .containsKey(TestUtils.intpas.get(1).getIdIntermediarioPa());
    assertThat(allData.getPsps())
        .containsKey(TestUtils.psps.get(0).getIdPsp())
        .containsKey(TestUtils.psps.get(1).getIdPsp());
    assertThat(allData.getChannels())
        .containsKey(TestUtils.canali.get(0).getIdCanale())
        .containsKey(TestUtils.canali.get(1).getIdCanale());
    assertThat(allData.getPaymentTypes())
        .containsKey(TestUtils.tipiVersamento.get(0).getTipoVersamento())
        .containsKey(TestUtils.tipiVersamento.get(0).getTipoVersamento());
    assertThat(allData.getPspChannelPaymentTypes())
        .containsKey(
            TestUtils.pspCanaliTv.get(0).getPsp().getIdPsp()
                + "_"
                + TestUtils.pspCanaliTv.get(0).getCanale().getIdCanale()
                + "_"
                + TestUtils.pspCanaliTv.get(0).getTipoVersamento().getTipoVersamento())
        .containsKey(
            TestUtils.pspCanaliTv.get(1).getPsp().getIdPsp()
                + "_"
                + TestUtils.pspCanaliTv.get(1).getCanale().getIdCanale()
                + "_"
                + TestUtils.pspCanaliTv.get(1).getTipoVersamento().getTipoVersamento());
    assertThat(allData.getPspInformations())
        .containsKey(TestUtils.psps.get(0).getIdPsp())
        .containsKey(TestUtils.psps.get(1).getIdPsp())
        .containsKey("FULL")
        .containsKey("EMPTY");
    assertThat(allData.getPspInformationTemplates())
        .containsKey(TestUtils.psps.get(0).getIdPsp())
        .containsKey(TestUtils.psps.get(1).getIdPsp());
    assertThat(allData.getPspBrokers())
        .containsKey(TestUtils.intpsp.get(0).getIdIntermediarioPsp())
        .containsKey(TestUtils.intpsp.get(1).getIdIntermediarioPsp());
    assertThat(allData.getFtpServers())
        .containsKey(TestUtils.ftpServers.get(0).getId().toString())
        .containsKey(TestUtils.ftpServers.get(1).getId().toString());
    assertThat(allData.getGdeConfigurations())
        .containsKey(
            TestUtils.gdeConfigurations.get(0).getPrimitiva()
                + "_"
                + TestUtils.gdeConfigurations.get(0).getType())
        .containsKey(
            TestUtils.gdeConfigurations.get(1).getPrimitiva()
                + "_"
                + TestUtils.gdeConfigurations.get(0).getType());
    assertThat(allData.getPlugins())
        .containsKey(TestUtils.plugins.get(0).getIdServPlugin())
        .containsKey(TestUtils.plugins.get(1).getIdServPlugin());
    assertThat(allData.getIbans())
        .containsKey(
            TestUtils.ibans.get(0).getPa().getIdDominio()
                + "-"
                + TestUtils.ibans.get(0).getIbanAccredito())
        .containsKey(
            TestUtils.ibans.get(1).getPa().getIdDominio()
                + "-"
                + TestUtils.ibans.get(1).getIbanAccredito());
    assertThat(allData.getCreditorInstitutionEncodings())
        .containsKey(TestUtils.pas.get(0).getIdDominio())
        .containsKey(TestUtils.pas.get(1).getIdDominio());
    assertThat(allData.getEncodings())
        .containsKey(TestUtils.encodings.get(0).getIdCodifica())
        .containsKey(TestUtils.encodings.get(1).getIdCodifica());
    assertThat(allData.getStations())
        .containsKey(TestUtils.stazioni.get(0).getIdStazione())
        .containsKey(TestUtils.stazioni.get(1).getIdStazione());
    assertThat(allData.getCreditorInstitutionStations())
        .containsKey(
            TestUtils.paStazioniPa.get(0).getFkStazione().getIdStazione()
                + "_"
                + TestUtils.paStazioniPa.get(0).getPa().getIdDominio()
                + "_"
                + TestUtils.paStazioniPa.get(0).getAuxDigit()
                + "_"
                + TestUtils.paStazioniPa.get(0).getProgressivo()
                + "_"
                + TestUtils.paStazioniPa.get(0).getSegregazione())
        .containsKey(
            TestUtils.paStazioniPa.get(1).getFkStazione().getIdStazione()
                + "_"
                + TestUtils.paStazioniPa.get(1).getPa().getIdDominio()
                + "_"
                + TestUtils.paStazioniPa.get(1).getAuxDigit()
                + "_"
                + TestUtils.paStazioniPa.get(1).getProgressivo()
                + "_"
                + TestUtils.paStazioniPa.get(1).getSegregazione());

    assertThat(allData.getCdsCategories())
        .containsKey(TestUtils.cdsCategorie.get(0).getDescription())
        .containsKey(TestUtils.cdsCategorie.get(1).getDescription());
    assertThat(allData.getCdsServices())
        .containsKey(TestUtils.cdsServizi.get(0).getIdServizio())
        .containsKey(TestUtils.cdsServizi.get(1).getIdServizio());
    assertThat(allData.getCdsSubjects())
        .containsKey(TestUtils.cdsSoggetti.get(0).getCreditorInstitutionCode())
        .containsKey(TestUtils.cdsSoggetti.get(1).getCreditorInstitutionCode());
    assertThat(allData.getCdsSubjectServices())
        .containsKey(TestUtils.cdsSoggettiServizi.get(0).getIdSoggettoServizio())
        .containsKey(TestUtils.cdsSoggettiServizi.get(1).getIdSoggettoServizio());
  }
}
