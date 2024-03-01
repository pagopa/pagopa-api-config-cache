package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.cache.controller.stakeholders.NodeCacheController;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.util.*;
import it.gov.pagopa.apiconfig.starter.repository.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class NodoConfigCacheTest {

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

  @InjectMocks private ConfigService configService;

  @BeforeEach
  void setUp() {
    org.springframework.test.util.ReflectionTestUtils.setField(configService, "keyV1Id", "value");
    org.springframework.test.util.ReflectionTestUtils.setField(configService, "keyV1", "value");
    org.springframework.test.util.ReflectionTestUtils.setField(configService, "keyV1InProgress", "value");
    org.springframework.test.util.ReflectionTestUtils.setField(configService, "saveDB", true);
    org.springframework.test.util.ReflectionTestUtils.setField(configService, "sendEvent", true);

    configService.postConstruct();
  }

  @Test
  void getCacheV1Id() {
    Map<String, Object> configDataV1Map = new HashMap<>();
    configDataV1Map.put(Constants.version,"12345");
    ConfigDataV1 configDataV11 = new ConfigDataV1();
    configDataV11.setVersion("12345");
    when(redisRepository.getStringByKeyId(anyString())).thenReturn(TestUtils.cacheId);
    when(redisRepository.getBooleanByKeyId(anyString())).thenReturn(true);
    when(redisRepository.getCache(anyString())).thenReturn(configDataV1Map);
    CacheVersion cacheV1Id = configService.getCacheV1Id("");
    assertThat(cacheV1Id.getVersion().equals(TestUtils.cacheId));
    Boolean inProgress = configService.getCacheV1InProgress("");
    assertThat(inProgress);

    Map<String, Object> configDataV1 = configService.loadFullCache();
    assertThat(configDataV1.get(Constants.version).equals(configDataV11.getVersion()));
  }

  @Test
  void testXls() throws Exception {
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
    when(informativePaMasterRepository.findAll()).thenReturn(TestUtils.informativePaMaster);
    when(jsonSerializer.serialize(any())).thenReturn("{}".getBytes(StandardCharsets.UTF_8));

    byte[] export = JsonToXls.convert(configService.newCacheV1(), false);
//    Files.write(Path.of("./target/output.xlsx"), export);

    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(export);
    XSSFWorkbook workbook = new XSSFWorkbook(byteArrayInputStream);
    assertThat(workbook.getNumberOfSheets()).isEqualTo(25);
    workbook.close();
    byteArrayInputStream.close();
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
    when(informativePaMasterRepository.findAll()).thenReturn(TestUtils.informativePaMaster);
    when(jsonSerializer.serialize(any())).thenReturn("{}".getBytes(StandardCharsets.UTF_8));

    ConfigDataV1 allData = ConfigDataUtil.cacheToConfigDataV1(configService.newCacheV1(), NodeCacheController.KEYS);
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
    //    assertThat(allData.getPspInformations())
    //        .containsKey(TestUtils.psps.get(0).getIdPsp())
    //        .containsKey(TestUtils.psps.get(1).getIdPsp())
    //        .containsKey("FULL")
    //        .containsKey("EMPTY");
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

  @Test
  void getCacheV1Keys() throws Exception {
    when(configurationKeysRepository.findAll()).thenReturn(TestUtils.mockConfigurationKeys);
    when(dizionarioMetadatiRepository.findAll()).thenReturn(TestUtils.mockMetadataDicts);
    when(paRepository.findAll()).thenReturn(TestUtils.pas);

    ConfigDataV1 allData = ConfigDataUtil.cacheToConfigDataV1(configService.newCacheV1(),NodeCacheController.KEYS);
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
    assertThat(allData.getCreditorInstitutionInformations() == null);
    assertThat(allData.getCreditorInstitutionBrokers() == null);
    assertThat(allData.getPsps() == null);
    assertThat(allData.getChannels() == null);
    assertThat(allData.getPaymentTypes() == null);
    assertThat(allData.getPspChannelPaymentTypes() == null);
    assertThat(allData.getPspInformations() == null);
    assertThat(allData.getPspInformationTemplates() == null);
    assertThat(allData.getPspBrokers() == null);
    assertThat(allData.getFtpServers() == null);
    assertThat(allData.getGdeConfigurations() == null);
    assertThat(allData.getPlugins() == null);
    assertThat(allData.getIbans() == null);
    assertThat(allData.getCreditorInstitutionEncodings() == null);
    assertThat(allData.getEncodings() == null);
    assertThat(allData.getStations() == null);
    assertThat(allData.getCreditorInstitutionStations() == null);
    assertThat(allData.getCdsCategories() == null);
    assertThat(allData.getCdsServices() == null);
    assertThat(allData.getCdsSubjects() == null);
    assertThat(allData.getCdsSubjectServices() == null);
  }
}
