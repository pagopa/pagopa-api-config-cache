package it.gov.pagopa.apiconfig.cache.service;

import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtCostiServizio;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtFasciaCostoServizio;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtIdentificazioneServizio;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtInformativaDetail;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtInformativaMaster;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtInformativaPSP;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtInformazioniServizio;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtListaConvenzioni;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtListaFasceCostoServizio;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtListaInformativaDetail;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtListaInformativePSP;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtListaInformazioniServizio;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtListaParoleChiave;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.StCodiceLingua;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.StParoleChiave;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.StTipoVersamento;
import it.gov.pagopa.apiconfig.cache.imported.controparti.CtContoAccredito;
import it.gov.pagopa.apiconfig.cache.imported.controparti.CtErogazione;
import it.gov.pagopa.apiconfig.cache.imported.controparti.CtErogazioneServizio;
import it.gov.pagopa.apiconfig.cache.imported.controparti.CtFasciaOraria;
import it.gov.pagopa.apiconfig.cache.imported.controparti.CtInformativaControparte;
import it.gov.pagopa.apiconfig.cache.imported.controparti.CtListaInformativeControparte;
import it.gov.pagopa.apiconfig.cache.imported.controparti.StTipoPeriodo;
import it.gov.pagopa.apiconfig.cache.imported.template.TplCostiServizio;
import it.gov.pagopa.apiconfig.cache.imported.template.TplFasciaCostoServizio;
import it.gov.pagopa.apiconfig.cache.imported.template.TplIdentificazioneServizio;
import it.gov.pagopa.apiconfig.cache.imported.template.TplInformativaDetail;
import it.gov.pagopa.apiconfig.cache.imported.template.TplInformativaMaster;
import it.gov.pagopa.apiconfig.cache.imported.template.TplInformativaPSP;
import it.gov.pagopa.apiconfig.cache.imported.template.TplInformazioniServizio;
import it.gov.pagopa.apiconfig.cache.imported.template.TplListaFasceCostoServizio;
import it.gov.pagopa.apiconfig.cache.imported.template.TplListaInformativaDetail;
import it.gov.pagopa.apiconfig.cache.imported.template.TplListaInformazioniServizio;
import it.gov.pagopa.apiconfig.cache.imported.template.TplListaParoleChiave;
import it.gov.pagopa.apiconfig.cache.model.NodeCacheKey;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsCategory;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsService;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsSubject;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.FtpServer;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.GdeConfiguration;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.MetadataDict;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.PaymentType;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.Plugin;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.CreditorInstitutionInformation;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.StationCreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.Channel;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.PspChannelPaymentType;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.PspInformation;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.JsonSerializer;
import it.gov.pagopa.apiconfig.starter.entity.Cache;
import it.gov.pagopa.apiconfig.starter.entity.CdiDetail;
import it.gov.pagopa.apiconfig.starter.entity.CdiFasciaCostoServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiInformazioniServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.starter.entity.CdiPreference;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaDetail;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaFasce;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaMaster;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.PspCanaleTipoVersamentoCanale;
import it.gov.pagopa.apiconfig.starter.repository.CacheRepository;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Service
@Transactional
public class ConfigService {

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

  private static String daCompilareFlusso =
      "DA COMPILARE (formato: [IDPSP]_dd-mm-yyyy - esempio: ESEMPIO_31-12-2001)";
  private static String daCompilare = "DA COMPILARE";
  private static String schemaInstance = "http://www.w3.org/2001/XMLSchema-instance";
  private static double costoConvenzioneFormat = 100d;

  private static String stakeholderPlaceholder = "{{stakeholder}}";

  @Value("${in_progress.ttl}")
  private long IN_PROGRESS_TTL;

  @Autowired private JsonSerializer jsonSerializer;
  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private RedisRepository redisRepository;
  @Autowired private CacheRepository cacheRepository;
  @Autowired private ConfigMapper modelMapper;
  @Autowired private ConfigurationKeysRepository configurationKeysRepository;
  @Autowired private IntermediariPaRepository intermediariPaRepository;
  @Autowired private IntermediariPspRepository intermediariPspRepository;
  @Autowired private CdsCategorieRepository cdsCategorieRepository;
  @Autowired private CdsSoggettoRepository cdsSoggettoRepository;
  @Autowired private CdsServizioRepository cdsServizioRepository;
  @Autowired private CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  @Autowired private GdeConfigRepository gdeConfigRepository;
  @Autowired private DizionarioMetadatiRepository dizionarioMetadatiRepository;
  @Autowired private FtpServersRepository ftpServersRepository;
  @Autowired private TipiVersamentoRepository tipiVersamentoRepository;
  @Autowired private WfespPluginConfRepository wfespPluginConfRepository;
  @Autowired private CodifichePaRepository codifichePaRepository;
  @Autowired private CodificheRepository codificheRepository;
  @Autowired private IbanValidiPerPaRepository ibanValidiPerPaRepository;
  @Autowired private StazioniRepository stazioniRepository;
  @Autowired private PaStazionePaRepository paStazioniRepository;
  @Autowired private PaRepository paRepository;
  @Autowired private CanaliViewRepository canaliViewRepository;

  @Autowired
  private PspCanaleTipoVersamentoCanaleRepository pspCanaleTipoVersamentoCanaleRepository;

  @Autowired private PspRepository pspRepository;
  @Autowired private CdiMasterValidRepository cdiMasterValidRepository;
  @Autowired private CdiDetailRepository cdiDetailRepository;
  @Autowired private CdiPreferenceRepository cdiPreferenceRepository;
  @Autowired private CdiInformazioniServizioRepository cdiInformazioniServizioRepository;
  @Autowired private CdiFasciaCostoServizioRepository cdiFasceRepository;
  @Autowired private InformativePaMasterRepository informativePaMasterRepository;
  @Autowired private InformativePaDetailRepository informativePaDetailRepository;
  @Autowired private InformativePaFasceRepository informativePaFasceRepository;

  private JAXBContext ctListaInformativePSPJaxbContext;
  private JAXBContext tplInformativaPSPJaxbContext;
  private JAXBContext ctListaInformativeControparteJaxbContext;

  @PostConstruct
  public void postConstruct() {
    try {
      ctListaInformativePSPJaxbContext = JAXBContext.newInstance(CtListaInformativePSP.class);
      tplInformativaPSPJaxbContext = JAXBContext.newInstance(TplInformativaPSP.class);
      ctListaInformativeControparteJaxbContext =
          JAXBContext.newInstance(CtListaInformativeControparte.class);
    } catch (JAXBException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }
  }

  public ConfigDataV1 loadFromRedis(String stakeholder) {
    String actualKey = getKeyV1(stakeholder);
    log.info("Initializing cache [" + actualKey + "]");
    return redisRepository.getConfigDataV1(actualKey);
  }

  public ConfigDataV1 newCacheV1(String stakeholder) throws IOException {
    return newCacheV1(stakeholder, Optional.empty());
  }

  public ConfigDataV1 newCacheV1(String stakeholder, Optional<NodeCacheKey[]> keys)
      throws IOException {

    setCacheV1InProgress(stakeholder);

    ConfigDataV1 configData = new ConfigDataV1();

    try {
      boolean allKeys = keys.isEmpty();
      List<NodeCacheKey> list =
          keys.map(k -> Arrays.asList(k)).orElse(new ArrayList<NodeCacheKey>());

      long startTime = System.nanoTime();

      if (allKeys || list.contains(NodeCacheKey.CREDITOR_INSTITUTION_BROKERS)) {
        List<BrokerCreditorInstitution> intpa = getBrokerDetails();
        HashMap<String, BrokerCreditorInstitution> intpamap = new HashMap<>();
        intpa.forEach(k -> intpamap.put(k.getBrokerCode(), k));
        configData.setCreditorInstitutionBrokers(intpamap);
      }

      if (allKeys || list.contains(NodeCacheKey.PSP_BROKERS)) {
        List<BrokerPsp> intpsp = getBrokerPspDetails();
        HashMap<String, BrokerPsp> intpspmap = new HashMap<>();
        intpsp.forEach(k -> intpspmap.put(k.getBrokerPspCode(), k));
        configData.setPspBrokers(intpspmap);
      }

      if (allKeys || list.contains(NodeCacheKey.CDS_CATEGORIES)) {
        List<CdsCategory> cdscats = getCdsCategories();
        HashMap<String, CdsCategory> cdscatsMap = new HashMap<>();
        cdscats.forEach(k -> cdscatsMap.put(k.getDescription(), k));
        configData.setCdsCategories(cdscatsMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CDS_SERVICES)) {
        List<CdsService> cdsServices = getCdsServices();
        HashMap<String, CdsService> cdsServicesMap = new HashMap<>();
        cdsServices.forEach(k -> cdsServicesMap.put(k.getIdentifier(), k));
        configData.setCdsServices(cdsServicesMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CDS_SUBJECTS)) {
        List<CdsSubject> cdsSubjects = getCdsSubjects();
        HashMap<String, CdsSubject> cdsSubjectsMap = new HashMap<>();
        cdsSubjects.forEach(k -> cdsSubjectsMap.put(k.getCreditorInstitutionCode(), k));
        configData.setCdsSubjects(cdsSubjectsMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CDS_SUBJECT_SERVICES)) {
        List<CdsSubjectService> cdsSubjectServices = getCdsSubjectServices();
        HashMap<String, CdsSubjectService> cdsSubjectServicesMap = new HashMap<>();
        cdsSubjectServices.forEach(k -> cdsSubjectServicesMap.put(k.getSubjectServiceId(), k));
        configData.setCdsSubjectServices(cdsSubjectServicesMap);
      }

      if (allKeys || list.contains(NodeCacheKey.GDE_CONFIGURATIONS)) {
        List<GdeConfiguration> gde = getGdeConfiguration();
        HashMap<String, GdeConfiguration> gdeMap = new HashMap<>();
        gde.forEach(k -> gdeMap.put(k.getIdentifier(), k));
        configData.setGdeConfigurations(gdeMap);
      }

      if (allKeys || list.contains(NodeCacheKey.METADATA_DICT)) {
        List<MetadataDict> meta = getMetadataDict();
        HashMap<String, MetadataDict> metaMap = new HashMap<>();
        meta.forEach(k -> metaMap.put(k.getKey(), k));
        configData.setMetadataDict(metaMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CONFIGURATIONS)) {
        List<ConfigurationKey> configurationKeyList = getConfigurationKeys();
        HashMap<String, ConfigurationKey> configMap = new HashMap<>();
        configurationKeyList.forEach(k -> configMap.put(k.getIdentifier(), k));
        configData.setConfigurations(configMap);
      }

      if (allKeys || list.contains(NodeCacheKey.FTP_SERVERS)) {
        List<FtpServer> ftpservers = getFtpServers();
        HashMap<String, FtpServer> ftpserversMap = new HashMap<>();
        ftpservers.forEach(k -> ftpserversMap.put(k.getId().toString(), k));
        configData.setFtpServers(ftpserversMap);
      }

      if (allKeys || list.contains(NodeCacheKey.LANGUAGES)) {
        HashMap<String, String> codiciLingua = new HashMap<>();
        codiciLingua.put("IT", "IT");
        codiciLingua.put("DE", "DE");
        configData.setLanguages(codiciLingua);
      }

      if (allKeys || list.contains(NodeCacheKey.PLUGINS)) {
        List<Plugin> plugins = getWfespPluginConfigurations();
        HashMap<String, Plugin> pluginsMap = new HashMap<>();
        plugins.forEach(k -> pluginsMap.put(k.getIdServPlugin(), k));
        configData.setPlugins(pluginsMap);
      }

      if (allKeys || list.contains(NodeCacheKey.PSPS)) {
        List<PaymentServiceProvider> psps = getAllPaymentServiceProviders();
        HashMap<String, PaymentServiceProvider> pspMap = new HashMap<>();
        psps.forEach(k -> pspMap.put(k.getPspCode(), k));
        configData.setPsps(pspMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CHANNELS)) {
        List<Channel> canali = getAllCanali();
        HashMap<String, Channel> canalimap = new HashMap<>();
        canali.forEach(k -> canalimap.put(k.getChannelCode(), k));
        configData.setChannels(canalimap);
      }

      if (allKeys || list.contains(NodeCacheKey.PAYMENT_TYPES)) {
        List<PaymentType> tipiv = getPaymentTypes();
        HashMap<String, PaymentType> tipivMap = new HashMap<>();
        tipiv.forEach(k -> tipivMap.put(k.getPaymentTypeCode(), k));
        configData.setPaymentTypes(tipivMap);
      }

      if (allKeys || list.contains(NodeCacheKey.PSP_CHANNEL_PAYMENT_TYPES)) {
        List<PspChannelPaymentType> pspChannels = getPaymentServiceProvidersChannels();
        HashMap<String, PspChannelPaymentType> pspChannelsMap = new HashMap<>();
        pspChannels.forEach(k -> pspChannelsMap.put(k.getIdentifier(), k));
        configData.setPspChannelPaymentTypes(pspChannelsMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CREDITOR_INSTITUTIONS)) {
        List<CreditorInstitution> pas = getCreditorInstitutions();
        HashMap<String, CreditorInstitution> pamap = new HashMap<>();
        pas.forEach(k -> pamap.put(k.getCreditorInstitutionCode(), k));
        configData.setCreditorInstitutions(pamap);
      }

      if (allKeys || list.contains(NodeCacheKey.ENCODINGS)) {
        List<Encoding> encodings = getEncodings();
        HashMap<String, Encoding> encodingsMap = new HashMap<>();
        encodings.forEach(k -> encodingsMap.put(k.getCodeType(), k));
        configData.setEncodings(encodingsMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CREDITOR_INSTITUTION_ENCODINGS)) {
        List<CreditorInstitutionEncoding> ciencodings = getCreditorInstitutionEncodings();
        HashMap<String, CreditorInstitutionEncoding> ciencodingsMap = new HashMap<>();
        ciencodings.forEach(k -> ciencodingsMap.put(k.getIdentifier(), k));
        configData.setCreditorInstitutionEncodings(ciencodingsMap);
      }

      if (allKeys || list.contains(NodeCacheKey.CREDITOR_INSTITUTION_STATIONS)) {
        List<StationCreditorInstitution> paspa = findAllPaStazioniPa();
        HashMap<String, StationCreditorInstitution> paspamap = new HashMap<>();
        paspa.forEach(k -> paspamap.put(k.getIdentifier(), k));
        configData.setCreditorInstitutionStations(paspamap);
      }

      if (allKeys || list.contains(NodeCacheKey.STATIONS)) {
        List<Station> stazioni = findAllStazioni();
        HashMap<String, Station> stazionimap = new HashMap<>();
        stazioni.forEach(k -> stazionimap.put(k.getStationCode(), k));
        configData.setStations(stazionimap);
      }

      if (allKeys || list.contains(NodeCacheKey.IBANS)) {
        List<Iban> ibans = getCurrentIbans();
        HashMap<String, Iban> ibansMap = new HashMap<>();
        ibans.forEach(k -> ibansMap.put(k.getIdentifier(), k));
        configData.setIbans(ibansMap);
      }

      if (allKeys
          || list.contains(NodeCacheKey.PSP_INFORMATIONS)
          || list.contains(NodeCacheKey.PSP_INFORMATION_TEMPLATES)) {
        Pair<List<PspInformation>, List<PspInformation>> informativePspAndTemplates =
            getInformativePspAndTemplates();

        if (allKeys || list.contains(NodeCacheKey.PSP_INFORMATIONS)) {
          List<PspInformation> infopsps = informativePspAndTemplates.getLeft();
          HashMap<String, PspInformation> infopspsMap = new HashMap<>();
          infopsps.forEach(k -> infopspsMap.put(k.getPsp(), k));
          configData.setPspInformations(infopspsMap);
        }

        if (allKeys || list.contains(NodeCacheKey.PSP_INFORMATION_TEMPLATES)) {
          List<PspInformation> infopspTemplates = informativePspAndTemplates.getRight();
          HashMap<String, PspInformation> infopspTemplatesMap = new HashMap<>();
          infopspTemplates.forEach(k -> infopspTemplatesMap.put(k.getPsp(), k));
          configData.setPspInformationTemplates(infopspTemplatesMap);
        }
      }

      if (allKeys || list.contains(NodeCacheKey.CREDITOR_INSTITUTION_INFORMATIONS)) {
        List<CreditorInstitutionInformation> infopas = getInformativePa();
        HashMap<String, CreditorInstitutionInformation> infopasMap = new HashMap<>();
        infopas.forEach(k -> infopasMap.put(k.getPa(), k));
        configData.setCreditorInstitutionInformations(infopasMap);
      }

      long endTime = System.nanoTime();
      long duration = (endTime - startTime) / 1000000;
      log.info("cache loaded in " + duration + "ms");

      configData.setVersion("" + endTime);

      String actualKey = getKeyV1(stakeholder);
      String actualKeyV1 = getKeyV1Id(stakeholder);

      if (saveDB) {
        log.info("saving on CACHE table " + configData.getVersion());
        try {
          cacheRepository.save(
              Cache.builder()
                  .id(configData.getVersion())
                  .cache(jsonSerializer.serialize(configData))
                  .time(ZonedDateTime.now())
                  .version(Constants.GZIP_JSON_V1 + "-" + appVersion)
                  .build());
        } catch (Exception e) {
          log.error("could not save on db", e);
        }
      }

      redisRepository.pushToRedisAsync(actualKey, actualKeyV1, configData);
    } catch (Exception e) {
      log.error("Errore creazione cache", e);
      removeCacheV1InProgress(stakeholder);
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }
    removeCacheV1InProgress(stakeholder);
    return configData;
  }

  public void removeCacheV1InProgress(String stakeholder) {
    String actualKeyV1 = getKeyV1InProgress(stakeholder);
    redisRepository.remove(actualKeyV1);
  }

  public void setCacheV1InProgress(String stakeholder) {
    String actualKeyV1 = getKeyV1InProgress(stakeholder);
    redisRepository.save(actualKeyV1, true, IN_PROGRESS_TTL);
  }

  public Boolean getCacheV1InProgress(String stakeholder) {
    String actualKeyV1 = getKeyV1InProgress(stakeholder);
    return redisRepository.getBooleanByKeyId(actualKeyV1);
  }

  public CacheVersion getCacheV1Id(String stakeholder) {
    String actualKeyV1 = getKeyV1Id(stakeholder);
    String cacheId =
        Optional.ofNullable(redisRepository.getStringByKeyId(actualKeyV1))
            .orElseThrow(() -> new AppException(AppError.CACHE_ID_NOT_FOUND, actualKeyV1));
    return new CacheVersion(cacheId);
  }

  public List<ConfigurationKey> getConfigurationKeys() {
    log.info("loading ConfigurationKeys");
    return modelMapper
        .modelMapper()
        .map(
            configurationKeysRepository.findAll(),
            new TypeToken<List<ConfigurationKey>>() {}.getType());
  }

  public List<BrokerCreditorInstitution> getBrokerDetails() {
    log.info("loading PaBrokers");
    return modelMapper
        .modelMapper()
        .map(
            intermediariPaRepository.findAll(),
            new TypeToken<List<BrokerCreditorInstitution>>() {}.getType());
  }

  public List<BrokerPsp> getBrokerPspDetails() {
    log.info("loading PspBrokers");
    return modelMapper
        .modelMapper()
        .map(intermediariPspRepository.findAll(), new TypeToken<List<BrokerPsp>>() {}.getType());
  }

  public List<CdsCategory> getCdsCategories() {
    log.info("loading CdsCategories");
    return modelMapper
        .modelMapper()
        .map(cdsCategorieRepository.findAll(), new TypeToken<List<CdsCategory>>() {}.getType());
  }

  public List<CdsService> getCdsServices() {
    log.info("loading CdsServices");
    return modelMapper
        .modelMapper()
        .map(
            cdsServizioRepository.findAllFetching(),
            new TypeToken<List<CdsService>>() {}.getType());
  }

  public List<CdsSubject> getCdsSubjects() {
    log.info("loading CdsSubjects");
    return modelMapper
        .modelMapper()
        .map(cdsSoggettoRepository.findAll(), new TypeToken<List<CdsSubject>>() {}.getType());
  }

  public List<CdsSubjectService> getCdsSubjectServices() {
    log.info("loading CdsSubjectServices");
    return modelMapper
        .modelMapper()
        .map(
            cdsSoggettoServizioRepository.findAllFetchingStations(),
            new TypeToken<List<CdsSubjectService>>() {}.getType());
  }

  public List<GdeConfiguration> getGdeConfiguration() {
    log.info("loading GdeConfigurations");
    return modelMapper
        .modelMapper()
        .map(gdeConfigRepository.findAll(), new TypeToken<List<GdeConfiguration>>() {}.getType());
  }

  public List<MetadataDict> getMetadataDict() {
    log.info("loading MetadataDicts");
    return modelMapper
        .modelMapper()
        .map(
            dizionarioMetadatiRepository.findAll(),
            new TypeToken<List<MetadataDict>>() {}.getType());
  }

  public List<FtpServer> getFtpServers() {
    log.info("loading FtpServers");
    return modelMapper
        .modelMapper()
        .map(ftpServersRepository.findAll(), new TypeToken<List<FtpServer>>() {}.getType());
  }

  public List<PaymentType> getPaymentTypes() {
    log.info("loading PaymentTypes");
    return modelMapper
        .modelMapper()
        .map(tipiVersamentoRepository.findAll(), new TypeToken<List<PaymentType>>() {}.getType());
  }

  public List<Plugin> getWfespPluginConfigurations() {
    log.info("loading Plugins");
    return modelMapper
        .modelMapper()
        .map(wfespPluginConfRepository.findAll(), new TypeToken<List<Plugin>>() {}.getType());
  }

  public List<Iban> getCurrentIbans() {
    log.info("loading Ibans");
    return modelMapper
        .modelMapper()
        .map(
            ibanValidiPerPaRepository.findAllFetchingPas(),
            new TypeToken<List<Iban>>() {}.getType());
  }

  public List<Station> findAllStazioni() {
    log.info("loading Stations");
    return modelMapper
        .modelMapper()
        .map(
            stazioniRepository.findAllFetchingIntermediario(),
            new TypeToken<List<Station>>() {}.getType());
  }

  public List<StationCreditorInstitution> findAllPaStazioniPa() {
    log.info("loading PaStations");
    return paStazioniRepository.findAllFetching().stream()
        .map(
            s ->
                new StationCreditorInstitution(
                    s.getPa().getIdDominio(),
                    s.getFkStazione().getIdStazione(),
                    s.getProgressivo(),
                    s.getAuxDigit(),
                    s.getSegregazione(),
                    s.getQuartoModello(),
                    s.getBroadcast(),
                    s.getFkStazione().getVersionePrimitive(),
                    s.getPagamentoSpontaneo()))
        .collect(Collectors.toList());
  }

  public List<CreditorInstitution> getCreditorInstitutions() {
    log.info("loading Pas");
    return modelMapper
        .modelMapper()
        .map(paRepository.findAll(), new TypeToken<List<CreditorInstitution>>() {}.getType());
  }

  public List<Channel> getAllCanali() {
    log.info("loading Channels");
    return modelMapper
        .modelMapper()
        .map(
            canaliViewRepository.findAllFetchingIntermediario(),
            new TypeToken<List<Channel>>() {}.getType());
  }

  public List<PspChannelPaymentType> getPaymentServiceProvidersChannels() {
    log.info("loading PspChannels");
    return pspCanaleTipoVersamentoCanaleRepository.findAllFetching().stream()
        .map(
            p ->
                new PspChannelPaymentType(
                    p.getPsp().getIdPsp(),
                    p.getCanale().getIdCanale(),
                    p.getTipoVersamento().getTipoVersamento()))
        .collect(Collectors.toList());
  }

  public List<PaymentServiceProvider> getAllPaymentServiceProviders() {
    log.info("loading Psps");
    return modelMapper
        .modelMapper()
        .map(pspRepository.findAll(), new TypeToken<List<PaymentServiceProvider>>() {}.getType());
  }

  public List<CreditorInstitutionEncoding> getCreditorInstitutionEncodings() {
    log.info("loading PaEncodings");
    return modelMapper
        .modelMapper()
        .map(
            codifichePaRepository.findAllFetchingCodifiche(),
            new TypeToken<List<CreditorInstitutionEncoding>>() {}.getType());
  }

  public List<Encoding> getEncodings() {
    log.info("loading Encodings");
    return modelMapper
        .modelMapper()
        .map(codificheRepository.findAll(), new TypeToken<List<Encoding>>() {}.getType());
  }

  private Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

  private String toXml(TplInformativaPSP element) {
    try {
      JAXBElement<TplInformativaPSP> informativaPSP =
          new it.gov.pagopa.apiconfig.cache.imported.template.ObjectFactory()
              .createInformativaPSP(element);
      Marshaller marshaller = tplInformativaPSPJaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaInstance);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.marshal(informativaPSP, baos);
      return encoder.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      log.error("error creating TplInformativaPSP", e);
      return e.toString();
    }
  }

  private String toXml(CtListaInformativePSP element) {
    try {
      JAXBElement<CtListaInformativePSP> informativaPSP =
          new it.gov.pagopa.apiconfig.cache.imported.catalogodati.ObjectFactory()
              .createListaInformativePSP(element);
      Marshaller marshaller = ctListaInformativePSPJaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaInstance);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.marshal(informativaPSP, baos);
      return encoder.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      log.error("error creating CtListaInformativePSP", e);
      return e.toString();
    }
  }

  private String toXml(CtListaInformativeControparte element) {
    try {
      JAXBElement<CtListaInformativeControparte> informativaPA =
          new it.gov.pagopa.apiconfig.cache.imported.controparti.ObjectFactory()
              .createListaInformativeControparte(element);
      Marshaller marshaller = ctListaInformativeControparteJaxbContext.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, schemaInstance);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.marshal(informativaPA, baos);
      return encoder.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      log.error("error creating CtListaInformativeControparte", e);
      return e.toString();
    }
  }

  public Pair<List<PspInformation>, List<PspInformation>> getInformativePspAndTemplates() {
    List<Psp> psps = pspRepository.findAll();
    List<CdiPreference> preferences = cdiPreferenceRepository.findAll();
    List<CdiFasciaCostoServizio> allFasce = cdiFasceRepository.findAll();
    List<CdiMasterValid> masters =
        StreamSupport.stream(cdiMasterValidRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    List<CdiDetail> details = cdiDetailRepository.findAll();
    List<CdiInformazioniServizio> allInformazioni = cdiInformazioniServizioRepository.findAll();

    List<PspInformation> informativePsp =
        new ArrayList<>(); // getInformativePsp(psps, masters, details, preferences, allFasce,
    // allInformazioni);
    List<PspInformation> templateInformativePsp = getTemplateInformativePsp(masters);

    return Pair.of(informativePsp, templateInformativePsp);
  }

  public List<PspInformation> getInformativePsp(
      List<Psp> psps,
      List<CdiMasterValid> masters,
      List<CdiDetail> details,
      List<CdiPreference> preferences,
      List<CdiFasciaCostoServizio> allFasce,
      List<CdiInformazioniServizio> allInformazioni) {
    log.info("loading InformativePsp");

    List<CtListaInformativePSP> informativePspSingle =
        masters.stream()
            .filter(
                m -> details.stream().anyMatch(d -> d.getFkCdiMaster().getId().equals(m.getId())))
            .map(
                cdiMaster -> {
                  Psp psp =
                      psps.stream()
                          .filter(p -> p.getObjId().equals(cdiMaster.getFkPsp().getObjId()))
                          .findFirst()
                          .get();
                  CtInformativaPSP ctInformativaPSP = new CtInformativaPSP();
                  ctInformativaPSP.setCodiceABI(psp.getAbi());
                  ctInformativaPSP.setCodiceBIC(psp.getBic());
                  ctInformativaPSP.setIdentificativoPSP(psp.getIdPsp());
                  ctInformativaPSP.setRagioneSociale(psp.getRagioneSociale());
                  CtInformativaMaster ctInformativaMaster = new CtInformativaMaster();
                  try {
                    ctInformativaMaster.setDataInizioValidita(
                        tsToXmlGC(cdiMaster.getDataInizioValidita()));
                  } catch (DatatypeConfigurationException e) {
                    throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
                  }
                  try {
                    ctInformativaMaster.setDataPubblicazione(
                        tsToXmlGC(cdiMaster.getDataPubblicazione()));
                  } catch (DatatypeConfigurationException e) {
                    throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
                  }
                  ctInformativaMaster.setLogoPSP("".getBytes(StandardCharsets.UTF_8));
                  ctInformativaMaster.setStornoPagamento(
                      Boolean.TRUE.equals(cdiMaster.getStornoPagamento()) ? 1 : 0);
                  ctInformativaMaster.setUrlInformazioniPSP(cdiMaster.getUrlInformazioniPsp());
                  ctInformativaMaster.setMarcaBolloDigitale(
                      Boolean.TRUE.equals(cdiMaster.getMarcaBolloDigitale()) ? 1 : 0);
                  ctInformativaPSP.setInformativaMaster(ctInformativaMaster);
                  ctInformativaPSP.setIdentificativoFlusso(cdiMaster.getIdInformativaPsp());

                  List<CtInformativaDetail> masterdetails =
                      details.stream()
                          .filter(d -> d.getFkCdiMaster().getId().equals(cdiMaster.getId()))
                          .filter(
                              d ->
                                  !d.getPspCanaleTipoVersamento()
                                      .getTipoVersamento()
                                      .getTipoVersamento()
                                      .equals("PPAY"))
                          .map(
                              cdiDetail -> {
                                PspCanaleTipoVersamentoCanale pspCanaleTipoVersamento =
                                    cdiDetail.getPspCanaleTipoVersamento();

                                CtIdentificazioneServizio ctIdentificazioneServizio =
                                    new CtIdentificazioneServizio();
                                ctIdentificazioneServizio.setNomeServizio(
                                    cdiDetail.getNomeServizio());
                                ctIdentificazioneServizio.setLogoServizio(
                                    "".getBytes(StandardCharsets.UTF_8));

                                List<CdiInformazioniServizio> it =
                                    allInformazioni.stream()
                                        .filter(
                                            ii ->
                                                ii.getFkCdiDetail()
                                                    .getId()
                                                    .equals(cdiDetail.getId()))
                                        .filter(info -> info.getCodiceLingua().equals("IT"))
                                        .collect(Collectors.toList());
                                CtListaInformazioniServizio ctListaInformazioniServizio =
                                    new CtListaInformazioniServizio();
                                if (it.size() > 0) {
                                  CtInformazioniServizio ctInformazioniServizio =
                                      new CtInformazioniServizio();
                                  ctInformazioniServizio.setDescrizioneServizio(
                                      it.get(0).getDescrizioneServizio());
                                  ctInformazioniServizio.setCodiceLingua(
                                      StCodiceLingua.fromValue(it.get(0).getCodiceLingua()));
                                  ctInformazioniServizio.setDisponibilitaServizio(
                                      it.get(0).getDisponibilitaServizio());
                                  ctInformazioniServizio.setUrlInformazioniCanale(
                                      it.get(0).getUrlInformazioniCanale());
                                  ctListaInformazioniServizio
                                      .getInformazioniServizio()
                                      .add(ctInformazioniServizio);
                                }

                                List<CtFasciaCostoServizio> fasce =
                                    allFasce.stream()
                                        .filter(
                                            fas ->
                                                fas.getFkCdiDetail()
                                                    .getId()
                                                    .equals(cdiDetail.getId()))
                                        .map(
                                            fascia -> {
                                              CtFasciaCostoServizio ctFasciaCostoServizio =
                                                  new CtFasciaCostoServizio();
                                              ctFasciaCostoServizio.setCostoFisso(
                                                  BigDecimal.valueOf(fascia.getCostoFisso())
                                                      .setScale(2, RoundingMode.FLOOR));
                                              ctFasciaCostoServizio.setImportoMassimoFascia(
                                                  BigDecimal.valueOf(fascia.getImportoMassimo())
                                                      .setScale(2, RoundingMode.FLOOR));
                                              ctFasciaCostoServizio.setValoreCommissione(
                                                  (BigDecimal.valueOf(fascia.getValoreCommissione())
                                                      .setScale(2, RoundingMode.FLOOR)));
                                              return ctFasciaCostoServizio;
                                            })
                                        .collect(Collectors.toList());
                                CtListaFasceCostoServizio ctListaFasceCostoServizio =
                                    new CtListaFasceCostoServizio();
                                ctListaFasceCostoServizio.getFasciaCostoServizio().addAll(fasce);

                                List<CdiPreference> cdiPreferenceStream =
                                    preferences.stream()
                                        .filter(
                                            pref ->
                                                pref.getCdiDetail()
                                                    .getId()
                                                    .equals(cdiDetail.getId()))
                                        .collect(Collectors.toList());
                                List<String> buyers =
                                    cdiPreferenceStream.stream()
                                        .map(p -> p.getBuyer())
                                        .collect(Collectors.toList());
                                CtListaConvenzioni listaConvenzioni = new CtListaConvenzioni();
                                listaConvenzioni.getCodiceConvenzione().addAll(buyers);

                                CtInformativaDetail ctInformativaDetail = new CtInformativaDetail();
                                ctInformativaDetail.setCanaleApp(
                                    cdiDetail.getCanaleApp().intValue());
                                ctInformativaDetail.setIdentificativoCanale(
                                    pspCanaleTipoVersamento.getCanale().getIdCanale());

                                List<Double> costiConvenzione =
                                    cdiPreferenceStream.stream()
                                        .map(p -> p.getCostoConvenzione() / costoConvenzioneFormat)
                                        .collect(Collectors.toList());

                                CtCostiServizio costiServizio = new CtCostiServizio();
                                costiServizio.setTipoCostoTransazione(1);
                                costiServizio.setTipoCommissione(0);
                                costiServizio.setListaFasceCostoServizio(ctListaFasceCostoServizio);
                                if (!costiConvenzione.isEmpty()) {
                                  costiServizio.setCostoConvenzione(
                                      BigDecimal.valueOf(costiConvenzione.get(0)));
                                }
                                ctInformativaDetail.setCostiServizio(costiServizio);

                                ctInformativaDetail.setPriorita(cdiDetail.getPriorita().intValue());
                                ctInformativaDetail.setListaConvenzioni(listaConvenzioni);
                                ctInformativaDetail.setIdentificativoIntermediario(
                                    pspCanaleTipoVersamento
                                        .getCanale()
                                        .getIntermediarioPsp()
                                        .getIdIntermediarioPsp());
                                ctInformativaDetail.setIdentificazioneServizio(
                                    ctIdentificazioneServizio);
                                ctInformativaDetail.setListaInformazioniServizio(
                                    ctListaInformazioniServizio);
                                if (cdiDetail.getTags() != null) {
                                  CtListaParoleChiave ctListaParoleChiave =
                                      new CtListaParoleChiave();
                                  ctListaParoleChiave
                                      .getParoleChiave()
                                      .addAll(
                                          Arrays.stream(cdiDetail.getTags().split(";"))
                                              .map(StParoleChiave::fromValue)
                                              .collect(Collectors.toList()));
                                  ctInformativaDetail.setListaParoleChiave(ctListaParoleChiave);
                                }
                                ctInformativaDetail.setModelloPagamento(
                                    cdiDetail.getModelloPagamento().intValue());
                                ctInformativaDetail.setTipoVersamento(
                                    StTipoVersamento.fromValue(
                                        pspCanaleTipoVersamento
                                            .getTipoVersamento()
                                            .getTipoVersamento()));
                                return ctInformativaDetail;
                              })
                          .collect(Collectors.toList());
                  CtListaInformativaDetail listaInformativaDetail = new CtListaInformativaDetail();
                  listaInformativaDetail.getInformativaDetail().addAll(masterdetails);
                  ctInformativaPSP.setListaInformativaDetail(listaInformativaDetail);

                  CtListaInformativePSP ctListaInformativePSP = new CtListaInformativePSP();
                  ctListaInformativePSP.getInformativaPSP().add(ctInformativaPSP);
                  return ctListaInformativePSP;
                })
            .collect(Collectors.toList());

    CtListaInformativePSP informativaPspFull = new CtListaInformativePSP();
    informativePspSingle.forEach(
        i -> informativaPspFull.getInformativaPSP().addAll(i.getInformativaPSP()));

    CtListaInformativePSP informativaEmpty = new CtListaInformativePSP();

    List<PspInformation> informativePspSingleCache =
        informativePspSingle.stream()
            .map(
                i ->
                    PspInformation.builder()
                        .psp(i.getInformativaPSP().get(0).getIdentificativoPSP())
                        .informativa(toXml(i))
                        .build())
            .collect(Collectors.toList());

    PspInformation informativaPSPFull =
        PspInformation.builder().psp("FULL").informativa(toXml(informativaPspFull)).build();

    PspInformation informativaPSPEmpty =
        PspInformation.builder().psp("EMPTY").informativa(toXml(informativaEmpty)).build();

    informativePspSingleCache.add(informativaPSPFull);
    informativePspSingleCache.add(informativaPSPEmpty);
    return informativePspSingleCache;
  }

  public List<PspInformation> getTemplateInformativePsp(List<CdiMasterValid> allMasters) {
    log.info("loading TemplateInformativePsp");
    List<Psp> psps = pspRepository.findAll();
    List<PspInformation> templates = new ArrayList<>();

    psps.forEach(
        psp -> {
          try {
            Optional<CdiMasterValid> masters =
                allMasters.stream()
                    .filter(m -> m.getFkPsp().getObjId().equals(psp.getObjId()))
                    .findFirst();
            TplInformativaPSP tplInformativaPSP = new TplInformativaPSP();
            tplInformativaPSP.setRagioneSociale(daCompilare);
            tplInformativaPSP.setIdentificativoPSP(daCompilare);
            tplInformativaPSP.setCodiceABI(
                Objects.isNull(psp.getAbi()) ? daCompilare : psp.getAbi());
            tplInformativaPSP.setCodiceBIC(
                Objects.isNull(psp.getBic()) ? daCompilare : psp.getBic());
            tplInformativaPSP.setIdentificativoFlusso(daCompilareFlusso);
            tplInformativaPSP.setMybankIDVS(
                Objects.isNull(psp.getCodiceMybank()) ? daCompilare : psp.getCodiceMybank());

            TplInformativaMaster tplInformativaMaster = new TplInformativaMaster();
            tplInformativaMaster.setLogoPSP(daCompilare);
            tplInformativaMaster.setDataInizioValidita(daCompilare);
            tplInformativaMaster.setDataPubblicazione(daCompilare);
            tplInformativaMaster.setUrlConvenzioniPSP(daCompilare);
            tplInformativaMaster.setUrlInformativaPSP(daCompilare);
            tplInformativaMaster.setUrlInformazioniPSP(daCompilare);
            tplInformativaMaster.setMarcaBolloDigitale(0);
            tplInformativaMaster.setStornoPagamento(0);
            tplInformativaPSP.setInformativaMaster(tplInformativaMaster);

            if (masters.isEmpty()) {
              TplListaInformativaDetail tplListaInformativaDetail = new TplListaInformativaDetail();
              tplListaInformativaDetail
                  .getInformativaDetail()
                  .add(makeTplInformativaDetail(null, null, null, null));
              tplInformativaPSP.setListaInformativaDetail(tplListaInformativaDetail);
              templates.add(new PspInformation(psp.getIdPsp(), toXml(tplInformativaPSP)));
            } else {
              tplInformativaPSP.setRagioneSociale(psp.getRagioneSociale());
              tplInformativaPSP.setIdentificativoPSP(psp.getIdPsp());
              TplListaInformativaDetail tplListaInformativaDetail = new TplListaInformativaDetail();
              masters
                  .get()
                  .getCdiDetail()
                  .forEach(
                      d ->
                          tplListaInformativaDetail
                              .getInformativaDetail()
                              .add(
                                  makeTplInformativaDetail(
                                      d.getPspCanaleTipoVersamento().getCanale().getIdCanale(),
                                      d.getPspCanaleTipoVersamento()
                                          .getCanale()
                                          .getIntermediarioPsp()
                                          .getIdIntermediarioPsp(),
                                      d.getPspCanaleTipoVersamento()
                                          .getTipoVersamento()
                                          .getTipoVersamento(),
                                      d.getModelloPagamento())));
              tplInformativaPSP.setListaInformativaDetail(tplListaInformativaDetail);
              templates.add(new PspInformation(psp.getIdPsp(), toXml(tplInformativaPSP)));
            }
          } catch (Exception e) {
            log.error(
                "errore creazione template informativa psp:"
                    + psp.getIdPsp()
                    + " error:"
                    + e.getMessage());
          }
        });

    return templates;
  }

  private TplInformativaDetail makeTplInformativaDetail(
      String idCanale, String idInter, String tv, Long modello) {
    TplInformativaDetail tplInformativaDetail = new TplInformativaDetail();
    tplInformativaDetail.setCanaleApp(daCompilare);
    tplInformativaDetail.setIdentificativoCanale(Objects.isNull(idCanale) ? daCompilare : idCanale);
    tplInformativaDetail.setPriorita(daCompilare);
    tplInformativaDetail.setTipoVersamento(
        Objects.isNull(tv)
            ? it.gov.pagopa.apiconfig.cache.imported.template.StTipoVersamento.BBT
            : it.gov.pagopa.apiconfig.cache.imported.template.StTipoVersamento.fromValue(tv));
    tplInformativaDetail.setModelloPagamento(Objects.isNull(modello) ? 0 : modello.intValue());
    tplInformativaDetail.setIdentificativoIntermediario(
        Objects.isNull(idInter) ? daCompilare : idInter);
    tplInformativaDetail.setServizioAlleImprese(null);

    TplIdentificazioneServizio tplIdentificazioneServizio = new TplIdentificazioneServizio();
    tplIdentificazioneServizio.setLogoServizio(daCompilare);
    tplIdentificazioneServizio.setNomeServizio(daCompilare);
    tplInformativaDetail.setIdentificazioneServizio(tplIdentificazioneServizio);

    TplCostiServizio tplCostiServizio = new TplCostiServizio();
    tplCostiServizio.setTipoCommissione("0");
    tplCostiServizio.setTipoCostoTransazione("0");
    TplFasciaCostoServizio tplFasciaCostoServizio = new TplFasciaCostoServizio();
    tplFasciaCostoServizio.setCostoFisso(daCompilare);
    tplFasciaCostoServizio.setImportoMassimoFascia(daCompilare);
    tplFasciaCostoServizio.setCostoFisso(daCompilare);
    List<TplFasciaCostoServizio> tplFasciaCostoServizios =
        Arrays.asList(tplFasciaCostoServizio, tplFasciaCostoServizio, tplFasciaCostoServizio);
    TplListaFasceCostoServizio fasce = new TplListaFasceCostoServizio();
    fasce.getFasciaCostoServizio().addAll(tplFasciaCostoServizios);
    tplCostiServizio.setListaFasceCostoServizio(fasce);
    tplInformativaDetail.setCostiServizio(tplCostiServizio);

    TplListaParoleChiave ks = new TplListaParoleChiave();
    ks.getParoleChiave().add(daCompilare);
    ks.getParoleChiave().add(daCompilare);
    ks.getParoleChiave().add(daCompilare);
    tplInformativaDetail.setListaParoleChiave(ks);

    TplListaInformazioniServizio info = new TplListaInformazioniServizio();

    Arrays.asList(
            it.gov.pagopa.apiconfig.cache.imported.template.StCodiceLingua.IT,
            it.gov.pagopa.apiconfig.cache.imported.template.StCodiceLingua.EN,
            it.gov.pagopa.apiconfig.cache.imported.template.StCodiceLingua.DE,
            it.gov.pagopa.apiconfig.cache.imported.template.StCodiceLingua.FR,
            it.gov.pagopa.apiconfig.cache.imported.template.StCodiceLingua.SL)
        .forEach(
            l -> {
              TplInformazioniServizio infoser = new TplInformazioniServizio();
              infoser.setCodiceLingua(
                  it.gov.pagopa.apiconfig.cache.imported.template.StCodiceLingua.IT);
              infoser.setDescrizioneServizio(daCompilare);
              infoser.setDescrizioneServizio(daCompilare);
              infoser.setUrlInformazioniCanale(daCompilare);
              infoser.setLimitazioniServizio(daCompilare);
              info.getInformazioniServizio().add(infoser);
            });
    tplInformativaDetail.setListaInformazioniServizio(info);
    return tplInformativaDetail;
  }

  private List<CtContoAccredito> manageContiAccredito(List<IbanValidiPerPa> ibans) {
    return ibans.stream()
        .map(
            iban -> {
              String idNegozio = null;
              if (iban.getIdMerchant() != null
                  && iban.getIdBancaSeller() != null
                  && iban.getChiaveAvvio() != null
                  && iban.getChiaveEsito() != null
                  && !iban.getIdMerchant().isEmpty()
                  && !iban.getIdBancaSeller().isEmpty()
                  && !iban.getChiaveAvvio().isEmpty()
                  && !iban.getChiaveEsito().isEmpty()) {
                idNegozio = iban.getIdMerchant();
              }
              CtContoAccredito ctContoAccredito = new CtContoAccredito();
              ctContoAccredito.setIbanAccredito(iban.getIbanAccredito());
              ctContoAccredito.setIdNegozio(idNegozio);
              ctContoAccredito.setSellerBank(iban.getIdBancaSeller());
              try {
                ctContoAccredito.setDataAttivazioneIban(tsToXmlGC(iban.getDataInizioValidita()));
              } catch (DatatypeConfigurationException e) {
                throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
              }
              return ctContoAccredito;
            })
        .collect(Collectors.toList());
  }

  public List<CreditorInstitutionInformation> getInformativePa() {
    log.info("loading InformativePa");
    List<IbanValidiPerPa> allIbans = ibanValidiPerPaRepository.findAllFetchingPas();
    List<InformativePaMaster> allMasters = informativePaMasterRepository.findAll();
    List<InformativePaFasce> allFasce = informativePaFasceRepository.findAll();
    List<Pa> pas = paRepository.findAll();

    List<CreditorInstitutionInformation> informativePaSingleCache = new ArrayList<>();
    CtListaInformativeControparte informativaPaFull = new CtListaInformativeControparte();
    AtomicLong count = new AtomicLong(0l);
    int max = pas.size();
    pas.forEach(
        pa -> {
          if (count.incrementAndGet() % 100 == 0) {
            log.info("Processed " + count.get() + " of " + max);
          }
          log.debug("Processing pa:" + pa.getIdDominio());
          CtListaInformativeControparte ctListaInformativeControparte =
              new CtListaInformativeControparte();

          CtInformativaControparte ctInformativaControparte = new CtInformativaControparte();
          ctInformativaControparte.setIdentificativoDominio(pa.getIdDominio());
          ctInformativaControparte.setRagioneSociale(pa.getRagioneSociale());
          ctInformativaControparte.setContactCenterEnteCreditore("contactCenterEnteCreditore");
          ctInformativaControparte.setPagamentiPressoPSP(
              Boolean.TRUE.equals(pa.getPagamentoPressoPsp()) ? 1 : 0);

          List<IbanValidiPerPa> ibans =
              allIbans.stream()
                  .filter(i -> i.getFkPa().equals(pa.getObjId()))
                  .collect(Collectors.toList());
          List<CtContoAccredito> contiaccredito = manageContiAccredito(ibans);
          ctInformativaControparte.getInformativaContoAccredito().addAll(contiaccredito);

          List<InformativePaMaster> masters =
              allMasters.stream()
                  .filter(m -> m.getFkPa().getObjId().equals(pa.getObjId()))
                  .collect(Collectors.toList());
          InformativePaMaster master = null;
          if (!masters.isEmpty()) {
            master = masters.get(0);
          }
          if (master != null) {
            try {
              ctInformativaControparte.setDataInizioValidita(
                  tsToXmlGC(master.getDataInizioValidita()));
            } catch (DatatypeConfigurationException e) {
              throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
            }
            List<InformativePaDetail> infodetails = master.getDetails();

            List<CtErogazione> disponibilita =
                infodetails.stream()
                    .filter(d -> d.getFlagDisponibilita())
                    .map(d -> infoDetailToCtErogazione(allFasce, d))
                    .collect(Collectors.toList());
            List<CtErogazione> indisponibilita =
                infodetails.stream()
                    .filter(d -> !d.getFlagDisponibilita())
                    .map(d -> infoDetailToCtErogazione(allFasce, d))
                    .collect(Collectors.toList());
            CtErogazioneServizio ctErogazioneServizio = new CtErogazioneServizio();
            ctErogazioneServizio.getDisponibilita().addAll(disponibilita);
            ctErogazioneServizio.getIndisponibilita().addAll(indisponibilita);
            ctInformativaControparte.setErogazioneServizio(ctErogazioneServizio);
            ctListaInformativeControparte.getInformativaControparte().add(ctInformativaControparte);
          } else if (!contiaccredito.isEmpty()) {
            ctInformativaControparte.setDataInizioValidita(
                contiaccredito.get(0).getDataAttivazioneIban());
            ctListaInformativeControparte.getInformativaControparte().add(ctInformativaControparte);
          }

          CreditorInstitutionInformation cii =
              CreditorInstitutionInformation.builder()
                  .pa(pa.getIdDominio())
                  .informativa(toXml(ctListaInformativeControparte))
                  .build();
          informativePaSingleCache.add(cii);
          if (pa.getEnabled()) {
            informativaPaFull
                .getInformativaControparte()
                .addAll(ctListaInformativeControparte.getInformativaControparte());
          }
          log.debug("Processed  pa:" + pa.getIdDominio());
        });

    log.debug("creating cache info full");

    CreditorInstitutionInformation informativaPAFull =
        CreditorInstitutionInformation.builder()
            .pa("FULL")
            .informativa(toXml(informativaPaFull))
            .build();

    informativePaSingleCache.add(informativaPAFull);
    return informativePaSingleCache;
  }

  private CtErogazione infoDetailToCtErogazione(
      List<InformativePaFasce> allFasce, InformativePaDetail det) {
    List<CtFasciaOraria> fasce = new ArrayList<>();
    try {
      fasce =
          allFasce.stream()
              .filter(f -> f.getFkInformativaPaDetail().getId().equals(det.getId()))
              .map(
                  f -> {
                    CtFasciaOraria fascia = new CtFasciaOraria();
                    try {
                      fascia.setFasciaOrariaDa(stringToXmlGCTime(f.getOraDa()));
                    } catch (DatatypeConfigurationException e) {
                      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
                    }
                    try {
                      fascia.setFasciaOrariaA(stringToXmlGCTime(f.getOraA()));
                    } catch (DatatypeConfigurationException e) {
                      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
                    }
                    return fascia;
                  })
              .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("error fasce detail" + det.getId());
    }
    CtErogazione ctErogazione = new CtErogazione();
    ctErogazione.setGiorno(det.getGiorno());
    if (det.getTipo() != null) {
      ctErogazione.setTipoPeriodo(StTipoPeriodo.fromValue(det.getTipo()));
    }
    ctErogazione.getFasciaOraria().addAll(fasce);
    return ctErogazione;
  }

  private XMLGregorianCalendar stringToXmlGCTime(String time)
      throws DatatypeConfigurationException {
    if (time == null) {
      return null;
    }
    LocalTime t = LocalTime.parse(time);
    return DatatypeFactory.newInstance()
        .newXMLGregorianCalendarTime(
            t.getHour(), t.getMinute(), t.getSecond(), DatatypeConstants.FIELD_UNDEFINED);
  }

  private XMLGregorianCalendar tsToXmlGC(Timestamp ts) throws DatatypeConfigurationException {
    if (ts == null) {
      return null;
    }
    ZonedDateTime dateTime = ts.toInstant().atZone(ZoneId.systemDefault());
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    return DatatypeFactory.newInstance().newXMLGregorianCalendar(formatter.format(dateTime));
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
