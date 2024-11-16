package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.*;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.StCodiceLingua;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.StParoleChiave;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.StTipoVersamento;
import it.gov.pagopa.apiconfig.cache.imported.controparti.*;
import it.gov.pagopa.apiconfig.cache.imported.template.*;
import it.gov.pagopa.apiconfig.cache.model.FullData;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsCategory;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsService;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsSubject;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.*;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.*;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.*;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.JsonSerializer;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import it.gov.pagopa.apiconfig.starter.entity.*;
import it.gov.pagopa.apiconfig.starter.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPOutputStream;

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

  @Value("#{'${sendEvent}'=='true'}")
  private Boolean sendEvent;

  private static String daCompilareFlusso =
      "DA COMPILARE (formato: [IDPSP]_dd-mm-yyyy - esempio: ESEMPIO_31-12-2001)";
  private static String daCompilare = "DA COMPILARE";
  private static String schemaInstance = "http://www.w3.org/2001/XMLSchema-instance";
  private static double costoConvenzioneFormat = 100d;

  private static String stakeholderPlaceholder = "{{stakeholder}}";

  @Value("${in_progress.ttl}")
  private long IN_PROGRESS_TTL;

  @Autowired private JsonSerializer jsonSerializer;
//  @Autowired private PlatformTransactionManager transactionManager;
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
  @Autowired private PspCanaleTipoVersamentoCanaleRepository pspCanaleTipoVersamentoCanaleRepository;
  @Autowired private PspRepository pspRepository;
  @Autowired private CdiMasterValidRepository cdiMasterValidRepository;
  @Autowired private CdiDetailRepository cdiDetailRepository;
  @Autowired private CdiPreferenceRepository cdiPreferenceRepository;
  @Autowired private CdiInformazioniServizioRepository cdiInformazioniServizioRepository;
  @Autowired private CdiFasciaCostoServizioRepository cdiFasceRepository;
  @Autowired private InformativePaMasterRepository informativePaMasterRepository;
  @Autowired private InformativePaDetailRepository informativePaDetailRepository;
  @Autowired private InformativePaFasceRepository informativePaFasceRepository;

  @Autowired private CacheEventHubService eventHubService;
  @Autowired private ObjectMapper objectMapper;

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

  public HashMap<String, Object> loadFullCache() throws IOException {
    log.info("Loading full cache");

    byte[] bytes = redisRepository.get(getKeyV1(Constants.FULL));
    byte[] unzipped = ZipUtils.unzip(bytes);
    JsonFactory jsonFactory = new JsonFactory();
    JsonParser jsonParser = jsonFactory.createParser(unzipped);
    FullData fulldata = objectMapper.readValue(jsonParser, FullData.class);
    jsonParser.close();

    HashMap<String, Object> configData = new HashMap<>();
    configData.put(Constants.VERSION,fulldata.getVersion());
    configData.put(Constants.TIMESTAMP,fulldata.getTimestamp());
    configData.put(Constants.CACHE_VERSION,fulldata.getCacheVersion());
    configData.put(Constants.CREDITOR_INSTITUTIONS,fulldata.getCreditorInstitutions());
    configData.put(Constants.CREDITOR_INSTITUTION_BROKERS,fulldata.getCreditorInstitutionBrokers());
    configData.put(Constants.STATIONS,fulldata.getStations());
    configData.put(Constants.CREDITOR_INSTITUTION_STATIONS,fulldata.getCreditorInstitutionStations());
    configData.put(Constants.ENCODINGS,fulldata.getEncodings());
    configData.put(Constants.CREDITOR_INSTITUTION_ENCODINGS,fulldata.getCreditorInstitutionEncodings());
    configData.put(Constants.IBANS,fulldata.getIbans());
    configData.put(Constants.CREDITOR_INSTITUTION_INFORMATIONS,fulldata.getCreditorInstitutionInformations());
    configData.put(Constants.PSPS,fulldata.getPsps());
    configData.put(Constants.PSP_BROKERS,fulldata.getPspBrokers());
    configData.put(Constants.PAYMENT_TYPES,fulldata.getPaymentTypes());
    configData.put(Constants.PSP_CHANNEL_PAYMENT_TYPES,fulldata.getPspChannelPaymentTypes());
    configData.put(Constants.PLUGINS,fulldata.getPlugins());
    configData.put(Constants.PSP_INFORMATION_TEMPLATES,fulldata.getPspInformationTemplates());
    configData.put(Constants.PSP_INFORMATIONS,fulldata.getPspInformations());
    configData.put(Constants.CHANNELS,fulldata.getChannels());
    configData.put(Constants.CDS_SERVICES,fulldata.getCdsServices());
    configData.put(Constants.CDS_SUBJECTS,fulldata.getCdsSubjects());
    configData.put(Constants.CDS_SUBJECT_SERVICES,fulldata.getCdsSubjectServices());
    configData.put(Constants.CDS_CATEGORIES,fulldata.getCdsCategories());
    configData.put(Constants.CONFIGURATIONS,fulldata.getConfigurations());
    configData.put(Constants.FTP_SERVERS,fulldata.getFtpServers());
    configData.put(Constants.LANGUAGES,fulldata.getLanguages());
    configData.put(Constants.GDE_CONFIGURATIONS,fulldata.getGdeConfigurations());
    configData.put(Constants.METADATA_DICT,fulldata.getMetadataDict());
    return configData;
  }

  public HashMap<String, Object> newCache() throws IOException {

    setCacheV1InProgress(Constants.FULL);

    HashMap<String, Object> configData = new HashMap<>();
    try {

      long startTime = System.nanoTime();

      JsonFactory jsonFactory = new JsonFactory();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
      OutputStreamWriter outwriter = new OutputStreamWriter(gzipOut);
      JsonGenerator jsonGenerator = jsonFactory.createGenerator(outwriter);
      jsonGenerator.writeStartObject();

        List<BrokerCreditorInstitution> intpa = getBrokerDetails();
        HashMap<String, Object> intpamap = new HashMap<>();
        intpa.forEach(k -> intpamap.put(k.getBrokerCode(), k));
        configData.put(Constants.CREDITOR_INSTITUTION_BROKERS,intpamap);
      appendMapToJson(jsonGenerator,Constants.CREDITOR_INSTITUTION_BROKERS,intpamap);

        List<BrokerPsp> intpsp = getBrokerPspDetails();
        HashMap<String, Object> intpspmap = new HashMap<>();
        intpsp.forEach(k -> intpspmap.put(k.getBrokerPspCode(), k));
        configData.put(Constants.PSP_BROKERS,intpspmap);
      appendMapToJson(jsonGenerator,Constants.PSP_BROKERS,intpspmap);

        List<CdsCategory> cdscats = getCdsCategories();
        HashMap<String, Object> cdscatsMap = new HashMap<>();
        cdscats.forEach(k -> cdscatsMap.put(k.getDescription(), k));
        configData.put(Constants.CDS_CATEGORIES,cdscatsMap);
      appendMapToJson(jsonGenerator,Constants.CDS_CATEGORIES,cdscatsMap);

        List<CdsService> cdsServices = getCdsServices();
        HashMap<String, Object> cdsServicesMap = new HashMap<>();
        cdsServices.forEach(k -> cdsServicesMap.put(k.getIdentifier(), k));
        configData.put(Constants.CDS_SERVICES,cdsServicesMap);
      appendMapToJson(jsonGenerator,Constants.CDS_SERVICES,cdsServicesMap);

        List<CdsSubject> cdsSubjects = getCdsSubjects();
        HashMap<String, Object> cdsSubjectsMap = new HashMap<>();
        cdsSubjects.forEach(k -> cdsSubjectsMap.put(k.getCreditorInstitutionCode(), k));
        configData.put(Constants.CDS_SUBJECTS,cdsSubjectsMap);
      appendMapToJson(jsonGenerator,Constants.CDS_SUBJECTS,cdsSubjectsMap);

        List<CdsSubjectService> cdsSubjectServices = getCdsSubjectServices();
        HashMap<String, Object> cdsSubjectServicesMap = new HashMap<>();
        cdsSubjectServices.forEach(k -> cdsSubjectServicesMap.put(k.getSubjectServiceId(), k));
        configData.put(Constants.CDS_SUBJECT_SERVICES,cdsSubjectServicesMap);
      appendMapToJson(jsonGenerator,Constants.CDS_SUBJECT_SERVICES,cdsSubjectServicesMap);

        List<GdeConfiguration> gde = getGdeConfiguration();
        HashMap<String, Object> gdeMap = new HashMap<>();
        gde.forEach(k -> gdeMap.put(k.getIdentifier(), k));
        configData.put(Constants.GDE_CONFIGURATIONS,gdeMap);
      appendMapToJson(jsonGenerator,Constants.GDE_CONFIGURATIONS,gdeMap);

        List<MetadataDict> meta = getMetadataDict();
        HashMap<String, Object> metaMap = new HashMap<>();
        meta.forEach(k -> metaMap.put(k.getKey(), k));
        configData.put(Constants.METADATA_DICT,metaMap);
      appendMapToJson(jsonGenerator,Constants.METADATA_DICT,metaMap);

        List<ConfigurationKey> configurationKeyList = getConfigurationKeys();
        HashMap<String, Object> configMap = new HashMap<>();
        configurationKeyList.forEach(k -> configMap.put(k.getIdentifier(), k));
        configData.put(Constants.CONFIGURATIONS,configMap);
      appendMapToJson(jsonGenerator,Constants.CONFIGURATIONS,configMap);

        List<FtpServer> ftpservers = getFtpServers();
        HashMap<String, Object> ftpserversMap = new HashMap<>();
        ftpservers.forEach(k -> ftpserversMap.put(k.getId().toString(), k));
        configData.put(Constants.FTP_SERVERS,ftpserversMap);
      appendMapToJson(jsonGenerator,Constants.FTP_SERVERS,ftpserversMap);

        HashMap<String, Object> codiciLingua = new HashMap<>();
        codiciLingua.put("IT", "IT");
        codiciLingua.put("DE", "DE");
        configData.put(Constants.LANGUAGES,codiciLingua);
      appendMapToJson(jsonGenerator,Constants.LANGUAGES,codiciLingua);

        List<Plugin> plugins = getWfespPluginConfigurations();
        HashMap<String, Object> pluginsMap = new HashMap<>();
        plugins.forEach(k -> pluginsMap.put(k.getIdServPlugin(), k));
        configData.put(Constants.PLUGINS,pluginsMap);
      appendMapToJson(jsonGenerator,Constants.PLUGINS,pluginsMap);

        List<PaymentServiceProvider> psps = getAllPaymentServiceProviders();
        HashMap<String, Object> pspMap = new HashMap<>();
        psps.forEach(k -> pspMap.put(k.getPspCode(), k));
        configData.put(Constants.PSPS,pspMap);
      appendMapToJson(jsonGenerator,Constants.PSPS,pspMap);

        List<Channel> canali = getAllCanali();
        HashMap<String, Object> canalimap = new HashMap<>();
        canali.forEach(k -> canalimap.put(k.getChannelCode(), k));
        configData.put(Constants.CHANNELS,canalimap);
      appendMapToJson(jsonGenerator,Constants.CHANNELS,canalimap);

        List<PaymentType> tipiv = getPaymentTypes();
        HashMap<String, Object> tipivMap = new HashMap<>();
        tipiv.forEach(k -> tipivMap.put(k.getPaymentTypeCode(), k));
        configData.put(Constants.PAYMENT_TYPES,tipivMap);
      appendMapToJson(jsonGenerator,Constants.PAYMENT_TYPES,tipivMap);

        List<PspChannelPaymentType> pspChannels = getPaymentServiceProvidersChannels();
        HashMap<String, Object> pspChannelsMap = new HashMap<>();
        pspChannels.forEach(k -> pspChannelsMap.put(k.getIdentifier(), k));
        configData.put(Constants.PSP_CHANNEL_PAYMENT_TYPES,pspChannelsMap);
      appendMapToJson(jsonGenerator,Constants.PSP_CHANNEL_PAYMENT_TYPES,pspChannelsMap);

        List<CreditorInstitution> pas = getCreditorInstitutions();
        HashMap<String, Object> pamap = new HashMap<>();
        pas.forEach(k -> pamap.put(k.getCreditorInstitutionCode(), k));
        configData.put(Constants.CREDITOR_INSTITUTIONS,pamap);
      appendMapToJson(jsonGenerator,Constants.CREDITOR_INSTITUTIONS,pamap);

        List<Encoding> encodings = getEncodings();
        HashMap<String, Object> encodingsMap = new HashMap<>();
        encodings.forEach(k -> encodingsMap.put(k.getCodeType(), k));
        configData.put(Constants.ENCODINGS,encodingsMap);
      appendMapToJson(jsonGenerator,Constants.ENCODINGS,encodingsMap);

        List<CreditorInstitutionEncoding> ciencodings = getCreditorInstitutionEncodings();
        HashMap<String, Object> ciencodingsMap = new HashMap<>();
        ciencodings.forEach(k -> ciencodingsMap.put(k.getIdentifier(), k));
        configData.put(Constants.CREDITOR_INSTITUTION_ENCODINGS,ciencodingsMap);
      appendMapToJson(jsonGenerator,Constants.CREDITOR_INSTITUTION_ENCODINGS,ciencodingsMap);

        List<StationCreditorInstitution> paspa = findAllPaStazioniPa();
        HashMap<String, Object> paspamap = new HashMap<>();
        paspa.forEach(k -> paspamap.put(k.getIdentifier(), k));
        configData.put(Constants.CREDITOR_INSTITUTION_STATIONS,paspamap);
      appendMapToJson(jsonGenerator,Constants.CREDITOR_INSTITUTION_STATIONS,paspamap);

        List<Station> stazioni = findAllStazioni();
        HashMap<String, Object> stazionimap = new HashMap<>();
        stazioni.forEach(k -> stazionimap.put(k.getStationCode(), k));
        configData.put(Constants.STATIONS,stazionimap);
      appendMapToJson(jsonGenerator,Constants.STATIONS,stazionimap);

        List<Iban> ibans = getCurrentIbans();
        HashMap<String, Object> ibansMap = new HashMap<>();
        ibans.forEach(k -> ibansMap.put(k.getIdentifier(), k));
        configData.put(Constants.IBANS,ibansMap);
      appendMapToJson(jsonGenerator,Constants.IBANS,ibansMap);

        Pair<List<PspInformation>, List<PspInformation>> informativePspAndTemplates =
            getInformativePspAndTemplates();

          List<PspInformation> infopsps = informativePspAndTemplates.getLeft();
          HashMap<String, Object> infopspsMap = new HashMap<>();
          infopsps.forEach(k -> infopspsMap.put(k.getPsp(), k));
        configData.put(Constants.PSP_INFORMATIONS,infopspsMap);
      appendMapToJson(jsonGenerator,Constants.PSP_INFORMATIONS,infopspsMap);

          List<PspInformation> infopspTemplates = informativePspAndTemplates.getRight();
          HashMap<String, Object> infopspTemplatesMap = new HashMap<>();
          infopspTemplates.forEach(k -> infopspTemplatesMap.put(k.getPsp(), k));
        configData.put(Constants.PSP_INFORMATION_TEMPLATES,infopspTemplatesMap);
      appendMapToJson(jsonGenerator,Constants.PSP_INFORMATION_TEMPLATES,infopspTemplatesMap);

        List<CreditorInstitutionInformation> infopas = getInformativePa();
        HashMap<String, Object> infopasMap = new HashMap<>();
        infopas.forEach(k -> infopasMap.put(k.getPa(), k));
        configData.put(Constants.CREDITOR_INSTITUTION_INFORMATIONS,infopasMap);
      appendMapToJson(jsonGenerator,Constants.CREDITOR_INSTITUTION_INFORMATIONS,infopasMap);

      ZonedDateTime now = ZonedDateTime.now();
      long endTime = System.nanoTime();
      String id = "" + endTime;
      String cacheVersion = Constants.GZIP_JSON_V1 + "-" + appVersion;
      configData.put(Constants.VERSION, id);
      configData.put(Constants.TIMESTAMP, now);
      configData.put(Constants.CACHE_VERSION, cacheVersion);

      appendObjectToJson(jsonGenerator, Constants.VERSION, id);
      appendObjectToJson(jsonGenerator, Constants.TIMESTAMP, now);
      appendObjectToJson(jsonGenerator, Constants.CACHE_VERSION, cacheVersion);

      jsonGenerator.writeEndObject();
      jsonGenerator.close();

      byte[] cachebyteArray = baos.toByteArray();

      long duration = (endTime - startTime) / 1000000;
      log.info("cache loaded in " + duration + "ms");



      String actualKey = getKeyV1(Constants.FULL);
      String actualKeyV1 = getKeyV1Id(Constants.FULL);

      log.info(String.format("saving on Redis %s %s", actualKey, actualKeyV1));
      redisRepository.pushToRedisAsync(actualKey, actualKeyV1, cachebyteArray, id.getBytes(StandardCharsets.UTF_8));

      // TODO remove this
      // TODO generate in async stakeholder cache
      if (saveDB) {
        log.info("saving on CACHE table " + configData.get(Constants.VERSION));
        try {
          HashMap<String, Object> cloned = (HashMap<String, Object>)configData.clone();
          cloned.remove(Constants.TIMESTAMP);
          cloned.remove(Constants.CACHE_VERSION);
          //cloned to remove data not in ConfigDataV1
          cacheRepository.save(
              Cache.builder()
                  .id(id)
                  .cache(jsonSerializer.serialize(cloned))
                  .time(now)
                  .version(getVersion())
                  .build());
          log.info("saved on CACHE table " + id);
        } catch (Exception e) {
          log.error("[ALERT] could not save on db", e);
        }
      }
    } catch (Exception e) {
      log.error("[ALERT] problem to generate cache", e);
      removeCacheV1InProgress(Constants.FULL);
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }
    removeCacheV1InProgress(Constants.FULL);
    return configData;
  }

  public void sendEvent(String id,ZonedDateTime now){
    if(sendEvent){
        try {
            eventHubService.publishEvent(id,now,Constants.GZIP_JSON_V1 + "-" + appVersion);
        } catch (JsonProcessingException e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
    }
  }

  private String getVersion() {
    String version = Constants.GZIP_JSON_V1 + "-" + appVersion;
    if (version.length() > 32) {
      return version.substring(0, 32);
    }
    return version;
  }

  public void removeCacheV1InProgress(String stakeholder) {
    String actualKeyV1 = getKeyV1InProgress(stakeholder);
    redisRepository.remove(actualKeyV1);
  }

  public void setCacheV1InProgress(String stakeholder) {
    String actualKeyV1 = getKeyV1InProgress(stakeholder);
    redisRepository.save(actualKeyV1, "1".getBytes(StandardCharsets.UTF_8), IN_PROGRESS_TTL);
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
    return paStazioniRepository
        .findAllFetching()
        .stream()
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
    return pspCanaleTipoVersamentoCanaleRepository
        .findAllFetching()
        .stream()
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
      return Constants.ENCODER.encodeToString(baos.toByteArray());
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
      return Constants.ENCODER.encodeToString(baos.toByteArray());
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
      return Constants.ENCODER.encodeToString(baos.toByteArray());
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
        masters
            .stream()
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
                      details
                          .stream()
                          .filter(d -> d.getFkCdiMaster().getId().equals(cdiMaster.getId()))
                          .filter(
                              d ->
                                  !d.getPspCanaleTipoVersamento()
                                      .getCanaleTipoVersamento()
                                      .getTipoVersamento()
                                      .equals("PPAY"))
                          .map(
                              cdiDetail -> {
                                var pspCanaleTipoVersamento =
                                    cdiDetail.getPspCanaleTipoVersamento();

                                CtIdentificazioneServizio ctIdentificazioneServizio =
                                    new CtIdentificazioneServizio();
                                ctIdentificazioneServizio.setNomeServizio(
                                    cdiDetail.getNomeServizio());
                                ctIdentificazioneServizio.setLogoServizio(
                                    "".getBytes(StandardCharsets.UTF_8));

                                List<CdiInformazioniServizio> it =
                                    allInformazioni
                                        .stream()
                                        .filter(
                                            ii ->
                                                ii.getFkCdiDetail()
                                                    .getId()
                                                    .equals(cdiDetail.getId()))
                                        .filter(info -> info.getCodiceLingua().equals("IT"))
                                        .collect(Collectors.toList());
                                CtListaInformazioniServizio ctListaInformazioniServizio =
                                    new CtListaInformazioniServizio();
                                if (!it.isEmpty()) {
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
                                    allFasce
                                        .stream()
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
                                    preferences
                                        .stream()
                                        .filter(
                                            pref ->
                                                pref.getCdiDetail()
                                                    .getId()
                                                    .equals(cdiDetail.getId()))
                                        .collect(Collectors.toList());
                                List<String> buyers =
                                    cdiPreferenceStream
                                        .stream()
                                        .map(p -> p.getBuyer())
                                        .collect(Collectors.toList());
                                CtListaConvenzioni listaConvenzioni = new CtListaConvenzioni();
                                listaConvenzioni.getCodiceConvenzione().addAll(buyers);

                                CtInformativaDetail ctInformativaDetail = new CtInformativaDetail();
                                ctInformativaDetail.setCanaleApp(
                                    cdiDetail.getCanaleApp().intValue());
                                ctInformativaDetail.setIdentificativoCanale(
                                    pspCanaleTipoVersamento
                                        .getCanaleTipoVersamento()
                                        .getCanale()
                                        .getIdCanale());

                                List<Double> costiConvenzione =
                                    cdiPreferenceStream
                                        .stream()
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
                                        .getCanaleTipoVersamento()
                                        .getCanale()
                                        .getFkIntermediarioPsp()
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
                                            .getCanaleTipoVersamento()
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
        informativePspSingle
            .stream()
            .map(
                i ->
                    PspInformation.builder()
                        .psp(i.getInformativaPSP().get(0).getIdentificativoPSP())
                        .informativa(toXml(i))
                        .build())
            .collect(Collectors.toList());

    PspInformation informativaPSPFull =
        PspInformation.builder().psp(Constants.FULL_INFORMATION).informativa(toXml(informativaPspFull)).build();

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
                allMasters
                    .stream()
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
                                      d.getPspCanaleTipoVersamento()
                                          .getCanaleTipoVersamento()
                                          .getCanale()
                                          .getIdCanale(),
                                      d.getPspCanaleTipoVersamento()
                                          .getCanaleTipoVersamento()
                                          .getCanale()
                                          .getFkIntermediarioPsp()
                                          .getIdIntermediarioPsp(),
                                      d.getPspCanaleTipoVersamento()
                                          .getCanaleTipoVersamento()
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
    return ibans
        .stream()
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
              allIbans
                  .stream()
                  .filter(i -> i.getFkPa().equals(pa.getObjId()))
                  .collect(Collectors.toList());
          List<CtContoAccredito> contiaccredito = manageContiAccredito(ibans);
          ctInformativaControparte.getInformativaContoAccredito().addAll(contiaccredito);

          List<InformativePaMaster> masters =
              allMasters
                  .stream()
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
                infodetails
                    .stream()
                    .filter(d -> d.getFlagDisponibilita())
                    .map(d -> infoDetailToCtErogazione(allFasce, d))
                    .collect(Collectors.toList());
            List<CtErogazione> indisponibilita =
                infodetails
                    .stream()
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
          log.debug("Processed pa:" + pa.getIdDominio());
        });

    log.debug("creating cache info full");

    CreditorInstitutionInformation informativaPAFull =
        CreditorInstitutionInformation.builder()
            .pa(Constants.FULL_INFORMATION)
            .informativa(toXml(informativaPaFull))
            .build();

    informativePaSingleCache.add(informativaPAFull);
    return informativePaSingleCache;
  }

  private CtErogazione infoDetailToCtErogazione(List<InformativePaFasce> allFasce, InformativePaDetail det) {
    List<CtFasciaOraria> fasce = new ArrayList<>();
    try {
      fasce =
          allFasce
              .stream()
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

  private XMLGregorianCalendar stringToXmlGCTime(String time) throws DatatypeConfigurationException {
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

  public void appendObjectToJson(JsonGenerator jsonGenerator,String fieldName, Object object) throws IOException {
      jsonGenerator.writeFieldName(fieldName);
      objectMapper.writeValue(jsonGenerator, object);
  }
  public void appendMapToJson(JsonGenerator jsonGenerator,String fieldName, Map<String,Object> objectMap) throws IOException {
    jsonGenerator.writeFieldName(fieldName);
    jsonGenerator.writeStartObject();
    for (Map.Entry<String, Object> entry:objectMap.entrySet()) {
      appendObjectToJson(jsonGenerator,entry.getKey(),entry.getValue());
    }
    jsonGenerator.writeEndObject();
  }
}
