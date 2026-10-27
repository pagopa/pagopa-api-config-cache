package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
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
import it.gov.pagopa.apiconfig.cache.model.FullData;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsCategory;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsService;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsSubject;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.FtpServer;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.GdeConfiguration;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.MetadataDict;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.PaymentType;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.Plugin;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.*;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.Channel;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.PspChannelPaymentType;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.PspInformation;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import it.gov.pagopa.apiconfig.starter.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaDetail;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaFasce;
import it.gov.pagopa.apiconfig.starter.entity.InformativePaMaster;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.repository.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPOutputStream;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamResult;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CacheConfigService {

  private static final String DA_COMPILARE_FLUSSO =
          "DA COMPILARE (formato: [IDPSP]_dd-mm-yyyy - esempio: ESEMPIO_31-12-2001)";
  private static final String DA_COMPILARE = "DA COMPILARE";

  private Map<String, Object> inMemoryCache;

  private final Boolean preload;
  private final String appVersion;
  private final Boolean sendEvent;
  private final long inProgressTtl;
  private final RedisRepository redisRepository;
  private final ModelMapper modelMapper;
  private final ConfigurationKeysRepository configurationKeysRepository;
  private final IntermediariPaRepository intermediariPaRepository;
  private final IntermediariPspRepository intermediariPspRepository;
  private final CdsCategorieRepository cdsCategorieRepository;
  private final CdsSoggettoRepository cdsSoggettoRepository;
  private final CdsServizioRepository cdsServizioRepository;
  private final CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  private final GdeConfigRepository gdeConfigRepository;
  private final DizionarioMetadatiRepository dizionarioMetadatiRepository;
  private final FtpServersRepository ftpServersRepository;
  private final TipiVersamentoRepository tipiVersamentoRepository;
  private final WfespPluginConfRepository wfespPluginConfRepository;
  private final CodifichePaRepository codifichePaRepository;
  private final CodificheRepository codificheRepository;
  private final IbanValidiPerPaRepository ibanValidiPerPaRepository;
  private final StazioniRepository stazioniRepository;
  private final PaStazionePaRepository paStazioniRepository;
  private final StationMaintenanceRepository stationMaintenanceRepository;
  private final PaRepository paRepository;
  private final CanaliViewRepository canaliViewRepository;
  private final PspCanaleTipoVersamentoCanaleRepository pspCanaleTipoVersamentoCanaleRepository;
  private final PspRepository pspRepository;
  private final CdiMasterValidRepository cdiMasterValidRepository;
  private final InformativePaMasterRepository informativePaMasterRepository;
  private final InformativePaFasceRepository informativePaFasceRepository;
  private final CacheEventHubService eventHubService;
  private final ObjectMapper objectMapper;
  private final CacheKeyUtils cacheKeyUtils;
  private final Jaxb2Marshaller jaxb2Marshaller;

  public CacheConfigService(
          Jaxb2Marshaller jaxb2Marshaller,
          CacheKeyUtils cacheKeyUtils,
          ObjectMapper objectMapper,
          CacheEventHubService eventHubService,
          InformativePaFasceRepository informativePaFasceRepository,
          InformativePaMasterRepository informativePaMasterRepository,
          CdiMasterValidRepository cdiMasterValidRepository,
          PspRepository pspRepository,
          PspCanaleTipoVersamentoCanaleRepository pspCanaleTipoVersamentoCanaleRepository,
          CanaliViewRepository canaliViewRepository,
          PaRepository paRepository,
          StationMaintenanceRepository stationMaintenanceRepository,
          PaStazionePaRepository paStazioniRepository,
          StazioniRepository stazioniRepository,
          IbanValidiPerPaRepository ibanValidiPerPaRepository,
          CodificheRepository codificheRepository,
          CodifichePaRepository codifichePaRepository,
          WfespPluginConfRepository wfespPluginConfRepository,
          TipiVersamentoRepository tipiVersamentoRepository,
          FtpServersRepository ftpServersRepository,
          DizionarioMetadatiRepository dizionarioMetadatiRepository,
          GdeConfigRepository gdeConfigRepository,
          CdsSoggettoServizioRepository cdsSoggettoServizioRepository,
          CdsServizioRepository cdsServizioRepository,
          CdsSoggettoRepository cdsSoggettoRepository,
          CdsCategorieRepository cdsCategorieRepository,
          IntermediariPspRepository intermediariPspRepository,
          IntermediariPaRepository intermediariPaRepository,
          ConfigurationKeysRepository configurationKeysRepository,
          ModelMapper modelMapper,
          RedisRepository redisRepository,
          @Value("${in_progress.ttl}") long inProgressTtl,
          @Value("#{'${sendEvent}'=='true'}") Boolean sendEvent,
          @Value("${info.application.version}") String appVersion,
          @Value("${preload:true}") Boolean preload
  ) {
    this.jaxb2Marshaller = jaxb2Marshaller;
    this.cacheKeyUtils = cacheKeyUtils;
    this.objectMapper = objectMapper;
    this.eventHubService = eventHubService;
    this.informativePaFasceRepository = informativePaFasceRepository;
    this.informativePaMasterRepository = informativePaMasterRepository;
    this.cdiMasterValidRepository = cdiMasterValidRepository;
    this.pspRepository = pspRepository;
    this.pspCanaleTipoVersamentoCanaleRepository = pspCanaleTipoVersamentoCanaleRepository;
    this.canaliViewRepository = canaliViewRepository;
    this.paRepository = paRepository;
    this.stationMaintenanceRepository = stationMaintenanceRepository;
    this.paStazioniRepository = paStazioniRepository;
    this.stazioniRepository = stazioniRepository;
    this.ibanValidiPerPaRepository = ibanValidiPerPaRepository;
    this.codificheRepository = codificheRepository;
    this.codifichePaRepository = codifichePaRepository;
    this.wfespPluginConfRepository = wfespPluginConfRepository;
    this.tipiVersamentoRepository = tipiVersamentoRepository;
    this.ftpServersRepository = ftpServersRepository;
    this.dizionarioMetadatiRepository = dizionarioMetadatiRepository;
    this.gdeConfigRepository = gdeConfigRepository;
    this.cdsSoggettoServizioRepository = cdsSoggettoServizioRepository;
    this.cdsServizioRepository = cdsServizioRepository;
    this.cdsSoggettoRepository = cdsSoggettoRepository;
    this.cdsCategorieRepository = cdsCategorieRepository;
    this.intermediariPspRepository = intermediariPspRepository;
    this.intermediariPaRepository = intermediariPaRepository;
    this.configurationKeysRepository = configurationKeysRepository;
    this.modelMapper = modelMapper;
    this.redisRepository = redisRepository;
    this.inProgressTtl = inProgressTtl;
    this.sendEvent = sendEvent;
    this.appVersion = appVersion;
    this.preload = preload;
  }

  @PostConstruct
  public void preloadKeysFromRedis() {
    if (Boolean.TRUE.equals(preload)) {
      try {
        inMemoryCache = loadFullCache();
      } catch (Exception e){
        log.warn("Could not load single keys cache from redis: {}", e.getMessage(), e);
        try {
          doCache();
          log.info("Cache generated, saved to Redis and sent refresh event on startup");
        } catch (Exception ex) {
          log.error("[ALERT] Failed to load and generate cache on startup", ex);
        }
      }
    }
  }

  /**
   * Retrieve the in memory cache.
   *
   * @return a map containing all the keys and values.
   * @throws AppException if the in memory cache is not initialized
   */
  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public Map<String, Object> getInMemoryCache() {
    if (inMemoryCache == null) {
      throw new AppException(AppError.CACHE_NOT_INITIALIZED, "FULL");
    }
    return inMemoryCache;
  }

  /**
   *  Retrieve the in memory cache if has been initialized otherwise start a
   *  new initialization and return the cache
   *
   * @return a map containing all the keys and values.
   */
  public Map<String, Object> getInMemoryCacheOrRefreshIfNotExist() {
    if (inMemoryCache == null) {
      doCache();
    }
    return inMemoryCache;
  }

  /**
   *  Start a new cache initialization, persist it both in memory and on Redis
   *  and send the cache refresh event on EventHub
   */
  public void doCache() {
    inMemoryCache = newCache();

    sendEvent(
            (String) inMemoryCache.get(Constants.VERSION),
            (ZonedDateTime) inMemoryCache.get(Constants.TIMESTAMP));
  }

  protected Map<String, Object> loadFullCache() throws IOException {
    log.info("Loading full cache");

    byte[] bytes = redisRepository.get(cacheKeyUtils.getCacheKey(Constants.FULL));
    if (bytes == null) {
      throw new AppException(AppError.CACHE_NOT_INITIALIZED, Constants.FULL);
    }
    FullData fulldata;
    try (InputStream in = ZipUtils.unzipToStream(bytes)) {
      fulldata = objectMapper.readValue(in, FullData.class);
    }

    HashMap<String, Object> configData = new HashMap<>();
    configData.put(Constants.VERSION, fulldata.getVersion());
    configData.put(Constants.TIMESTAMP, fulldata.getTimestamp());
    configData.put(Constants.CACHE_VERSION, fulldata.getCacheVersion());
    configData.put(Constants.CREDITOR_INSTITUTIONS, fulldata.getCreditorInstitutions());
    configData.put(Constants.CREDITOR_INSTITUTION_BROKERS, fulldata.getCreditorInstitutionBrokers());
    configData.put(Constants.STATIONS, fulldata.getStations());
    configData.put(Constants.CREDITOR_INSTITUTION_STATIONS, fulldata.getCreditorInstitutionStations());
    configData.put(Constants.MAINTENANCE_STATIONS, fulldata.getMaintenanceStations());
    configData.put(Constants.ENCODINGS, fulldata.getEncodings());
    configData.put(Constants.CREDITOR_INSTITUTION_ENCODINGS, fulldata.getCreditorInstitutionEncodings());
    configData.put(Constants.IBANS, fulldata.getIbans());
    configData.put(Constants.CREDITOR_INSTITUTION_INFORMATIONS, fulldata.getCreditorInstitutionInformations());
    configData.put(Constants.PSPS, fulldata.getPsps());
    configData.put(Constants.PSP_BROKERS, fulldata.getPspBrokers());
    configData.put(Constants.PAYMENT_TYPES, fulldata.getPaymentTypes());
    configData.put(Constants.PSP_CHANNEL_PAYMENT_TYPES, fulldata.getPspChannelPaymentTypes());
    configData.put(Constants.PLUGINS, fulldata.getPlugins());
    configData.put(Constants.PSP_INFORMATION_TEMPLATES, fulldata.getPspInformationTemplates());
    configData.put(Constants.PSP_INFORMATIONS, fulldata.getPspInformations());
    configData.put(Constants.CHANNELS, fulldata.getChannels());
    configData.put(Constants.CDS_SERVICES, fulldata.getCdsServices());
    configData.put(Constants.CDS_SUBJECTS, fulldata.getCdsSubjects());
    configData.put(Constants.CDS_SUBJECT_SERVICES, fulldata.getCdsSubjectServices());
    configData.put(Constants.CDS_CATEGORIES, fulldata.getCdsCategories());
    configData.put(Constants.CONFIGURATIONS, fulldata.getConfigurations());
    configData.put(Constants.FTP_SERVERS, fulldata.getFtpServers());
    configData.put(Constants.LANGUAGES, fulldata.getLanguages());
    configData.put(Constants.GDE_CONFIGURATIONS, fulldata.getGdeConfigurations());
    configData.put(Constants.METADATA_DICT, fulldata.getMetadataDict());
    return configData;
  }

  public Map<String, Object> newCache() {
    setCacheInProgress();

    HashMap<String, Object> configData = new HashMap<>();
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      long startTime = System.nanoTime();

      JsonFactory jsonFactory = new JsonFactory();
      byte[] cacheByteArray;
      long endTime;
      String id;
      try (GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
          OutputStreamWriter streamWriter = new OutputStreamWriter(gzipOut);
          JsonGenerator jsonGenerator = jsonFactory.createGenerator(streamWriter)) {
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

      List<MaintenanceStation> maintenanceStations = findAllStationMaintenance();
      HashMap<String, Object> maintenanceStationsMap = new HashMap<>();
      maintenanceStations.forEach(k -> maintenanceStationsMap.put(k.getStationCode(), k));
      configData.put(Constants.MAINTENANCE_STATIONS, maintenanceStationsMap);
      appendMapToJson(jsonGenerator,Constants.MAINTENANCE_STATIONS, maintenanceStationsMap);

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

      configData.put(Constants.PSP_INFORMATIONS,new HashMap<>());
      appendMapToJson(jsonGenerator,Constants.PSP_INFORMATIONS,new HashMap<>());

      List<PspInformation> infopspTemplates = getInformativeTemplates();
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
      ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);
      endTime = System.nanoTime();
      id = "" + endTime;
      String cacheVersion = getVersion();
      configData.put(Constants.VERSION, id);
      configData.put(Constants.TIMESTAMP, romeDateTime);
      configData.put(Constants.CACHE_VERSION, cacheVersion);

      appendObjectToJson(jsonGenerator, Constants.VERSION, id);
      appendObjectToJson(jsonGenerator, Constants.TIMESTAMP, DateTimeUtils.getString(now));
      appendObjectToJson(jsonGenerator, Constants.CACHE_VERSION, cacheVersion);

      jsonGenerator.writeEndObject();
      }

      cacheByteArray = baos.toByteArray();

      long duration = (endTime - startTime) / 1000000;
      log.info(String.format("%s cache loaded in %s ms", Constants.FULL, duration));

      String actualKey = cacheKeyUtils.getCacheKey(Constants.FULL);
      String actualKeyV1 = cacheKeyUtils.getCacheIdKey(Constants.FULL);

      log.info(String.format("Saving on Redis %s %s %s", actualKey, actualKeyV1, id));
      redisRepository.pushToRedisAsync(actualKey, actualKeyV1, cacheByteArray, id.getBytes(StandardCharsets.UTF_8));
    } catch (Exception e) {
      log.error("[ALERT] problem to generate cache", e);
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    } finally {
      removeCacheInProgress();
    }
    return configData;
  }

  public void sendEvent(String id, ZonedDateTime now) {
    if (Boolean.TRUE.equals(sendEvent)) {
        try {
            eventHubService.publishEvent(id,now,getVersion());
        } catch (JsonProcessingException e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
    }
  }

  private String getVersion() {
    String version = Constants.GZIP_JSON + "-" + appVersion;
    if (version.length() > 32) {
      return version.substring(0, 32);
    }
    return version;
  }

  public void removeCacheInProgress() {
    String actualKeyV1 = cacheKeyUtils.getCacheKeyInProgress(Constants.FULL);
    redisRepository.remove(actualKeyV1);
  }

  private void setCacheInProgress() {
    String actualKeyV1 = cacheKeyUtils.getCacheKeyInProgress(Constants.FULL);
    redisRepository.save(actualKeyV1, "1".getBytes(StandardCharsets.UTF_8), inProgressTtl);
  }

  @Transactional(propagation = Propagation.NOT_SUPPORTED)
  public Boolean getCacheInProgress() {
    String actualKeyV1 = cacheKeyUtils.getCacheKeyInProgress(Constants.FULL);
    return redisRepository.getBooleanByKeyId(actualKeyV1);
  }

  public List<ConfigurationKey> getConfigurationKeys() {
    log.info("loading ConfigurationKeys");
    return modelMapper
        .map(
            configurationKeysRepository.findAll(),
            new TypeToken<List<ConfigurationKey>>() {}.getType());
  }

  public List<BrokerCreditorInstitution> getBrokerDetails() {
    log.info("loading PaBrokers");
    return modelMapper
        .map(
            intermediariPaRepository.findAll(),
            new TypeToken<List<BrokerCreditorInstitution>>() {}.getType());
  }

  public List<BrokerPsp> getBrokerPspDetails() {
    log.info("loading PspBrokers");
    return modelMapper
        .map(intermediariPspRepository.findAll(), new TypeToken<List<BrokerPsp>>() {}.getType());
  }

  public List<CdsCategory> getCdsCategories() {
    log.info("loading CdsCategories");
    return modelMapper
        .map(cdsCategorieRepository.findAll(), new TypeToken<List<CdsCategory>>() {}.getType());
  }

  public List<CdsService> getCdsServices() {
    log.info("loading CdsServices");
    return modelMapper
        .map(
            cdsServizioRepository.findAllFetching(),
            new TypeToken<List<CdsService>>() {}.getType());
  }

  public List<CdsSubject> getCdsSubjects() {
    log.info("loading CdsSubjects");
    return modelMapper
        .map(cdsSoggettoRepository.findAll(), new TypeToken<List<CdsSubject>>() {}.getType());
  }

  public List<CdsSubjectService> getCdsSubjectServices() {
    log.info("loading CdsSubjectServices");
    return modelMapper
        .map(
            cdsSoggettoServizioRepository.findAllFetchingStations(),
            new TypeToken<List<CdsSubjectService>>() {}.getType());
  }

  public List<GdeConfiguration> getGdeConfiguration() {
    log.info("loading GdeConfigurations");
    return modelMapper
        .map(gdeConfigRepository.findAll(), new TypeToken<List<GdeConfiguration>>() {}.getType());
  }

  public List<MetadataDict> getMetadataDict() {
    log.info("loading MetadataDicts");
    return modelMapper
        .map(
            dizionarioMetadatiRepository.findAll(),
            new TypeToken<List<MetadataDict>>() {}.getType());
  }

  public List<FtpServer> getFtpServers() {
    log.info("loading FtpServers");
    return modelMapper
        .map(ftpServersRepository.findAll(), new TypeToken<List<FtpServer>>() {}.getType());
  }

  public List<PaymentType> getPaymentTypes() {
    log.info("loading PaymentTypes");
    return modelMapper
        .map(tipiVersamentoRepository.findAll(), new TypeToken<List<PaymentType>>() {}.getType());
  }

  public List<Plugin> getWfespPluginConfigurations() {
    log.info("loading Plugins");
    return modelMapper
        .map(wfespPluginConfRepository.findAll(), new TypeToken<List<Plugin>>() {}.getType());
  }

  public List<Iban> getCurrentIbans() {
    log.info("loading Ibans");
    return modelMapper
        .map(
            ibanValidiPerPaRepository.findAllFetchingPas(),
            new TypeToken<List<Iban>>() {}.getType());
  }

  public List<Station> findAllStazioni() {
    log.info("loading Stations");
    return modelMapper
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
                    s.getPagamentoSpontaneo(),
                    s.getAca(),
                    s.getStandin()))
        .collect(Collectors.toList());
  }

  public List<MaintenanceStation> findAllStationMaintenance() {
    log.info("loading StationMaintenance");
    return stationMaintenanceRepository
            .findAll()
            .stream()
            .map(
                    s ->
                            new MaintenanceStation(
                                    s.getStation().getIdStazione(),
                                    s.getStartDateTime(),
                                    s.getEndDateTime(),
                                    s.getStandIn()))
            .collect(Collectors.toList());
  }

  public List<CreditorInstitution> getCreditorInstitutions() {
    log.info("loading Pas");
    return modelMapper
        .map(paRepository.findAll(), new TypeToken<List<CreditorInstitution>>() {}.getType());
  }

  public List<Channel> getAllCanali() {
    log.info("loading Channels");
    return modelMapper
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
        .map(pspRepository.findAll(), new TypeToken<List<PaymentServiceProvider>>() {}.getType());
  }

  public List<CreditorInstitutionEncoding> getCreditorInstitutionEncodings() {
    log.info("loading PaEncodings");
    return modelMapper
        .map(
            codifichePaRepository.findAllFetchingCodifiche(),
            new TypeToken<List<CreditorInstitutionEncoding>>() {}.getType());
  }

  public List<Encoding> getEncodings() {
    log.info("loading Encodings");
    return modelMapper
        .map(codificheRepository.findAll(), new TypeToken<List<Encoding>>() {}.getType());
  }

  private String toXml(TplInformativaPSP element) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      JAXBElement<TplInformativaPSP> informativaPSP =
          new it.gov.pagopa.apiconfig.cache.imported.template.ObjectFactory()
              .createInformativaPSP(element);
      jaxb2Marshaller.marshal(informativaPSP, new StreamResult(baos));
      return Constants.ENCODER.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      log.error("error creating TplInformativaPSP", e);
      return e.toString();
    }
  }

  private String toXml(CtListaInformativeControparte element) {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      JAXBElement<CtListaInformativeControparte> informativaPA =
          new it.gov.pagopa.apiconfig.cache.imported.controparti.ObjectFactory()
              .createListaInformativeControparte(element);
      jaxb2Marshaller.marshal(informativaPA, new StreamResult(baos));
      return Constants.ENCODER.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      log.error("error creating CtListaInformativeControparte", e);
      return e.toString();
    }
  }

  public List<PspInformation> getInformativeTemplates() {
    List<CdiMasterValid> masters =
        StreamSupport.stream(cdiMasterValidRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    return getTemplateInformativePsp(masters);
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
            tplInformativaPSP.setRagioneSociale(DA_COMPILARE);
            tplInformativaPSP.setIdentificativoPSP(DA_COMPILARE);
            tplInformativaPSP.setCodiceABI(
                Objects.isNull(psp.getAbi()) ? DA_COMPILARE : psp.getAbi());
            tplInformativaPSP.setCodiceBIC(
                Objects.isNull(psp.getBic()) ? DA_COMPILARE : psp.getBic());
            tplInformativaPSP.setIdentificativoFlusso(DA_COMPILARE_FLUSSO);
            tplInformativaPSP.setMybankIDVS(
                Objects.isNull(psp.getCodiceMybank()) ? DA_COMPILARE : psp.getCodiceMybank());

            TplInformativaMaster tplInformativaMaster = new TplInformativaMaster();
            tplInformativaMaster.setLogoPSP(DA_COMPILARE);
            tplInformativaMaster.setDataInizioValidita(DA_COMPILARE);
            tplInformativaMaster.setDataPubblicazione(DA_COMPILARE);
            tplInformativaMaster.setUrlConvenzioniPSP(DA_COMPILARE);
            tplInformativaMaster.setUrlInformativaPSP(DA_COMPILARE);
            tplInformativaMaster.setUrlInformazioniPSP(DA_COMPILARE);
            tplInformativaMaster.setMarcaBolloDigitale(0);
            tplInformativaMaster.setStornoPagamento(0);
            tplInformativaPSP.setInformativaMaster(tplInformativaMaster);

//            if (masters.isEmpty()) {
              TplListaInformativaDetail tplListaInformativaDetail = new TplListaInformativaDetail();
              tplListaInformativaDetail
                  .getInformativaDetail()
                  .add(makeTplInformativaDetail(null, null, null, null));
              tplInformativaPSP.setListaInformativaDetail(tplListaInformativaDetail);
              templates.add(new PspInformation(psp.getIdPsp(), toXml(tplInformativaPSP)));
              // TODO Remember to uncomment this, do we need it?
//            } else {
//              tplInformativaPSP.setRagioneSociale(psp.getRagioneSociale());
//              tplInformativaPSP.setIdentificativoPSP(psp.getIdPsp());
//              TplListaInformativaDetail tplListaInformativaDetail = new TplListaInformativaDetail();
//              masters
//                  .get()
//                  .getCdiDetail()
//                  .forEach(
//                      d ->
//                          tplListaInformativaDetail
//                              .getInformativaDetail()
//                              .add(
//                                  makeTplInformativaDetail(
//                                      d.getPspCanaleTipoVersamento()
//                                          .getCanaleTipoVersamento()
//                                          .getCanale()
//                                          .getIdCanale(),
//                                      d.getPspCanaleTipoVersamento()
//                                          .getCanaleTipoVersamento()
//                                          .getCanale()
//                                          .getFkIntermediarioPsp()
//                                          .getIdIntermediarioPsp(),
//                                      d.getPspCanaleTipoVersamento()
//                                          .getCanaleTipoVersamento()
//                                          .getTipoVersamento()
//                                          .getTipoVersamento(),
//                                      d.getModelloPagamento())));
//              tplInformativaPSP.setListaInformativaDetail(tplListaInformativaDetail);
//              templates.add(new PspInformation(psp.getIdPsp(), toXml(tplInformativaPSP)));
//            }
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
    tplInformativaDetail.setCanaleApp(DA_COMPILARE);
    tplInformativaDetail.setIdentificativoCanale(Objects.isNull(idCanale) ? DA_COMPILARE : idCanale);
    tplInformativaDetail.setPriorita(DA_COMPILARE);
    tplInformativaDetail.setTipoVersamento(
        Objects.isNull(tv)
            ? it.gov.pagopa.apiconfig.cache.imported.template.StTipoVersamento.BBT
            : it.gov.pagopa.apiconfig.cache.imported.template.StTipoVersamento.fromValue(tv));
    tplInformativaDetail.setModelloPagamento(Objects.isNull(modello) ? 0 : modello.intValue());
    tplInformativaDetail.setIdentificativoIntermediario(
        Objects.isNull(idInter) ? DA_COMPILARE : idInter);
    tplInformativaDetail.setServizioAlleImprese(null);

    TplIdentificazioneServizio tplIdentificazioneServizio = new TplIdentificazioneServizio();
    tplIdentificazioneServizio.setLogoServizio(DA_COMPILARE);
    tplIdentificazioneServizio.setNomeServizio(DA_COMPILARE);
    tplInformativaDetail.setIdentificazioneServizio(tplIdentificazioneServizio);

    TplCostiServizio tplCostiServizio = new TplCostiServizio();
    tplCostiServizio.setTipoCommissione("0");
    tplCostiServizio.setTipoCostoTransazione("0");
    TplFasciaCostoServizio tplFasciaCostoServizio = new TplFasciaCostoServizio();
    tplFasciaCostoServizio.setCostoFisso(DA_COMPILARE);
    tplFasciaCostoServizio.setImportoMassimoFascia(DA_COMPILARE);
    tplFasciaCostoServizio.setCostoFisso(DA_COMPILARE);
    List<TplFasciaCostoServizio> tplFasciaCostoServizios =
        Arrays.asList(tplFasciaCostoServizio, tplFasciaCostoServizio, tplFasciaCostoServizio);
    TplListaFasceCostoServizio fasce = new TplListaFasceCostoServizio();
    fasce.getFasciaCostoServizio().addAll(tplFasciaCostoServizios);
    tplCostiServizio.setListaFasceCostoServizio(fasce);
    tplInformativaDetail.setCostiServizio(tplCostiServizio);

    TplListaParoleChiave ks = new TplListaParoleChiave();
    ks.getParoleChiave().add(DA_COMPILARE);
    ks.getParoleChiave().add(DA_COMPILARE);
    ks.getParoleChiave().add(DA_COMPILARE);
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
              infoser.setDescrizioneServizio(DA_COMPILARE);
              infoser.setDescrizioneServizio(DA_COMPILARE);
              infoser.setUrlInformazioniCanale(DA_COMPILARE);
              infoser.setLimitazioniServizio(DA_COMPILARE);
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

	  Map<Long, List<IbanValidiPerPa>> ibanByPa = allIbans.stream()
			  .collect(Collectors.groupingBy(IbanValidiPerPa::getFkPa));

	  Map<Long, List<InformativePaMaster>> masterByPa = allMasters.stream()
			  .collect(Collectors.groupingBy(m -> m.getFkPa().getObjId()));

	  Map<Long, List<InformativePaFasce>> fasceByDetail = 
			  allFasce.stream()
			  .filter(f -> f.getFkInformativaPaDetail() != null)
			  .collect(Collectors.groupingBy(f -> f.getFkInformativaPaDetail().getId()));

	  List<CreditorInstitutionInformation> informativePaSingleCache = new ArrayList<>();
	  CtListaInformativeControparte informativaPaFull = new CtListaInformativeControparte();
	  AtomicLong count = new AtomicLong(0L);
	  int max = pas.size();

	  pas.forEach(pa -> {
		  if (count.incrementAndGet() % 100 == 0) {
			  log.info("Processed " + count.get() + " of " + max);
		  }
		  log.debug("Processing pa:" + pa.getIdDominio());

		  CtListaInformativeControparte ctListaInformativeControparte = new CtListaInformativeControparte();
		  CtInformativaControparte ctInformativaControparte = new CtInformativaControparte();
		  ctInformativaControparte.setIdentificativoDominio(pa.getIdDominio());
		  ctInformativaControparte.setRagioneSociale(pa.getRagioneSociale());
		  ctInformativaControparte.setContactCenterEnteCreditore("contactCenterEnteCreditore");
		  ctInformativaControparte.setPagamentiPressoPSP(Boolean.TRUE.equals(pa.getPagamentoPressoPsp()) ? 1 : 0);

		  List<IbanValidiPerPa> ibans = ibanByPa.getOrDefault(pa.getObjId(), Collections.emptyList());
		  List<CtContoAccredito> contiaccredito = manageContiAccredito(ibans);
		  ctInformativaControparte.getInformativaContoAccredito().addAll(contiaccredito);

		  List<InformativePaMaster> masters = masterByPa.getOrDefault(pa.getObjId(), Collections.emptyList());
		  InformativePaMaster master = masters.isEmpty() ? null : masters.get(0);

		  if (master != null) {
			  try {
				  ctInformativaControparte.setDataInizioValidita(tsToXmlGC(master.getDataInizioValidita()));
			  } catch (DatatypeConfigurationException e) {
				  throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
			  }

			  List<InformativePaDetail> infodetails = master.getDetails();
			  List<InformativePaFasce> fasce = infodetails.stream()
					  .flatMap(detail -> fasceByDetail.getOrDefault(detail.getId(), Collections.emptyList()).stream())
					  .collect(Collectors.toList());

			  List<CtErogazione> disponibilita = infodetails.stream()
					  .filter(InformativePaDetail::getFlagDisponibilita)
					  .map(d -> infoDetailToCtErogazione(fasce, d))
					  .collect(Collectors.toList());

			  List<CtErogazione> indisponibilita = infodetails.stream()
					  .filter(d -> !d.getFlagDisponibilita())
					  .map(d -> infoDetailToCtErogazione(fasce, d))
					  .collect(Collectors.toList());

			  CtErogazioneServizio ctErogazioneServizio = new CtErogazioneServizio();
			  ctErogazioneServizio.getDisponibilita().addAll(disponibilita);
			  ctErogazioneServizio.getIndisponibilita().addAll(indisponibilita);
			  ctInformativaControparte.setErogazioneServizio(ctErogazioneServizio);
			  ctListaInformativeControparte.getInformativaControparte().add(ctInformativaControparte);

		  } else if (!contiaccredito.isEmpty()) {
			  ctInformativaControparte.setDataInizioValidita(contiaccredito.get(0).getDataAttivazioneIban());
			  ctListaInformativeControparte.getInformativaControparte().add(ctInformativaControparte);
		  }

		  CreditorInstitutionInformation cii = CreditorInstitutionInformation.builder()
				  .pa(pa.getIdDominio())
				  .informativa(toXml(ctListaInformativeControparte))
				  .build();
		  informativePaSingleCache.add(cii);

		  if (Boolean.TRUE.equals(pa.getEnabled())) {
			  informativaPaFull.getInformativaControparte()
			  .addAll(ctListaInformativeControparte.getInformativaControparte());
		  }
		  log.debug("Processed pa:" + pa.getIdDominio());
	  });

	  log.debug("creating cache info full");
	  CreditorInstitutionInformation informativaPAFull = CreditorInstitutionInformation.builder()
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
