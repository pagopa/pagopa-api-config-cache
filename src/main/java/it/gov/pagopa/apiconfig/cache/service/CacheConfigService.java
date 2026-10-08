package it.gov.pagopa.apiconfig.cache.service;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.imported.catalogodati.CtListaInformativePSP;
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
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
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
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@Slf4j
@Service
@Transactional
public class CacheConfigService {

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CacheMetadata {
    private String version;
    private String id;
    private ZonedDateTime timestamp;
    private String cacheVersion;
  }

  @Value("${info.application.version}")
  private String APP_VERSION;

  @Value("#{'${sendEvent}'=='true'}")
  private Boolean SEND_EVENT;

  private static String DA_COMPILARE_FLUSSO =
      "DA COMPILARE (formato: [IDPSP]_dd-mm-yyyy - esempio: ESEMPIO_31-12-2001)";
  private static String DA_COMPILARE = "DA COMPILARE";
  private static String SCHEMAM_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
  private static double COSTO_CONVENZIONE_FORMAT = 100d;

  @Value("${in_progress.ttl}")
  private long IN_PROGRESS_TTL;

  @Autowired private RedisRepository redisRepository;
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
  @Autowired private StationMaintenanceRepository stationMaintenanceRepository;
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

  @Autowired private CacheKeyUtils cacheKeyUtils;
  private JAXBContext ctListaInformativePSPJaxbContext;
  private JAXBContext tplInformativaPSPJaxbContext;
  private JAXBContext ctListaInformativeControparteJaxbContext;

  @PostConstruct
  public void postConstruct() {
    try {
      ctListaInformativePSPJaxbContext = JAXBContext.newInstance(CtListaInformativePSP.class);
      tplInformativaPSPJaxbContext = JAXBContext.newInstance(TplInformativaPSP.class);
      ctListaInformativeControparteJaxbContext = JAXBContext.newInstance(CtListaInformativeControparte.class);
    } catch (JAXBException e) {
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    }
  }

  public HashMap<String, Object> loadFullCache() throws IOException {
    log.info("Loading full cache");

    return loadAndDecompressFromRedis();
  }

  public CacheMetadata  newCache() {
    setCacheInProgress();

    try {
      long startTime = System.nanoTime();

      JsonFactory jsonFactory = new JsonFactory();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
      OutputStreamWriter outwriter = new OutputStreamWriter(gzipOut, StandardCharsets.UTF_8);
      JsonGenerator jsonGenerator = jsonFactory.createGenerator(outwriter);
      jsonGenerator.writeStartObject();

      //Brokers
      writeBrokerDetailsToJson(jsonGenerator);

      //Broker PSP
      writeBrokerPspDetailsToJson(jsonGenerator);

      //CDS Categories
      writeCdsCategoryToJson(jsonGenerator);

      //CDS Services
      writeCdsServiceToJson(jsonGenerator);

      //CDS Subjects
      writeCdsSubjectsToJson(jsonGenerator);

      //CDS Subject Services
      writeCdsSubjectServicesToJson(jsonGenerator);

      //GDE Configuration
      writeGdeConfigurationToJson(jsonGenerator);

      //Metadata Dict
      writeMetadataDictToJson(jsonGenerator);

      //Configuration Keys
      writeConfigurationKeysToJson(jsonGenerator);

      //FTP Servers
      writeFtpServersToJson(jsonGenerator);

      //Languages
      writeLanguagesToJson(jsonGenerator);

      // Plugins
      writePluginsToJson(jsonGenerator);

      //PSPs
      writePaymentServiceProvidersToJson(jsonGenerator);

      //Channels
      writeChannelsToJson(jsonGenerator);

      //Payment Types
      writePaymentTypesToJson(jsonGenerator);

      //PSP Channel Payment Types
      writePspChannelPaymentTypesToJson(jsonGenerator);

      //Creditor Institutions
      writeCreditorInstitutionsToJson(jsonGenerator);

      //Encodings
      writeEncodingsToJson(jsonGenerator);

      //Creditor Institution Encodings
      writeCreditorInstitutionEncodingsToJson(jsonGenerator);

      //Station Creditor Institutions
      writeStationCreditorInstitutionsToJson(jsonGenerator);

      //Maintenance Stations
      writeMaintenanceStationsToJson(jsonGenerator);

      //Stations
      writeStationsToJson(jsonGenerator);

      //IBANs
      writeIbansToJson(jsonGenerator);

      //PSP Informations
      writePspInformationsToJson(jsonGenerator);

      //PSP Information Templates
      writePspInformationTemplatesToJson(jsonGenerator);

      //Creditor Institution Informations
      writeCreditorInstitutionInformationsToJson(jsonGenerator);

      // Metadata finale
      ZonedDateTime now = ZonedDateTime.now();
      String id = "" + System.nanoTime();
      String cacheVersion = getVersion();

      jsonGenerator.writeFieldName(Constants.VERSION);
      objectMapper.writeValue(jsonGenerator, id);

      jsonGenerator.writeFieldName(Constants.TIMESTAMP);
      objectMapper.writeValue(jsonGenerator, DateTimeUtils.getString(now));

      jsonGenerator.writeFieldName(Constants.CACHE_VERSION);
      objectMapper.writeValue(jsonGenerator, cacheVersion);

      byte[] cacheByteArray = baos.toByteArray();

      jsonGenerator.writeEndObject();
      jsonGenerator.flush();
      jsonGenerator.close();
      outwriter.close();
      gzipOut.close();

      long endTime = System.nanoTime();
      long duration = (endTime - startTime) / 1000000;
      log.info(String.format("%s cache loaded in %s ms", Constants.FULL, duration));

      String actualKey = cacheKeyUtils.getCacheKey(Constants.FULL);
      String actualKeyV1 = cacheKeyUtils.getCacheIdKey(Constants.FULL);

      log.info(String.format("Saving on Redis %s %s %s", actualKey, actualKeyV1, id));
      redisRepository.pushToRedisAsync(actualKey, actualKeyV1, cacheByteArray,
          id.getBytes(StandardCharsets.UTF_8));

      return CacheMetadata.builder()
          .version(id)
          .id(id)
          .timestamp(now)
          .cacheVersion(cacheVersion)
          .build();

    } catch (Exception e) {
      log.error("[ALERT] problem to generate cache", e);
      removeCacheInProgress();
      throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
    } finally {
      removeCacheInProgress();
    }
  }

  public HashMap<String, Object> loadAndDecompressFromRedis() throws IOException {
    log.info("Loading and decompressing cache from Redis (one-time)");

    byte[] bytes = redisRepository.get(cacheKeyUtils.getCacheKey(Constants.FULL));

    if (bytes == null) {
      throw new AppException(AppError.CACHE_NOT_INITIALIZED, "FULL");
    }

    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    GZIPInputStream gzipIn = new GZIPInputStream(bais);
    InputStreamReader reader = new InputStreamReader(gzipIn, StandardCharsets.UTF_8);
    JsonFactory jsonFactory = new JsonFactory();
    JsonParser jsonParser = jsonFactory.createParser(reader);
    try {
      FullData fulldata = objectMapper.readValue(jsonParser, FullData.class);
      jsonParser.close();
      reader.close();
      gzipIn.close();

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

      log.info("Cache decompressed successfully from Redis");
      return configData;
    } finally {
      jsonParser.close();
      reader.close();
      gzipIn.close();
    }
  }


  private void writeBrokerDetailsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<BrokerCreditorInstitution> intpa = getBrokerDetails();
    jsonGenerator.writeFieldName(Constants.CREDITOR_INSTITUTION_BROKERS);
    jsonGenerator.writeStartObject();
    for (BrokerCreditorInstitution broker : intpa) {
      jsonGenerator.writeFieldName(broker.getBrokerCode());
      objectMapper.writeValue(jsonGenerator, broker);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CREDITOR_INSTITUTION_BROKERS - " + intpa.size() + " items");
  }

  private void writeBrokerPspDetailsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<BrokerPsp> intpsp = getBrokerPspDetails();
    jsonGenerator.writeFieldName(Constants.PSP_BROKERS);
    jsonGenerator.writeStartObject();
    for (BrokerPsp broker : intpsp) {
      jsonGenerator.writeFieldName(broker.getBrokerPspCode());
      objectMapper.writeValue(jsonGenerator, broker);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: PSP_BROKERS - " + intpsp.size() + " items");
  }

  private void writeCdsCategoryToJson(JsonGenerator jsonGenerator) throws IOException {
    List<CdsCategory> cdscats = getCdsCategories();
    jsonGenerator.writeFieldName(Constants.CDS_CATEGORIES);
    jsonGenerator.writeStartObject();
    for (CdsCategory cat : cdscats) {
      jsonGenerator.writeFieldName(cat.getDescription());
      objectMapper.writeValue(jsonGenerator, cat);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CDS_CATEGORIES - " + cdscats.size() + " items");
  }

  private void writeCdsServiceToJson(JsonGenerator jsonGenerator) throws IOException {
    List<CdsService> cdsServices = getCdsServices();
    jsonGenerator.writeFieldName(Constants.CDS_SERVICES);
    jsonGenerator.writeStartObject();
    for (CdsService service : cdsServices) {
      jsonGenerator.writeFieldName(service.getIdentifier());
      objectMapper.writeValue(jsonGenerator, service);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CDS_SERVICES - " + cdsServices.size() + " items");
  }

  private void writeCdsSubjectsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<CdsSubject> cdsSubjects = getCdsSubjects();
    jsonGenerator.writeFieldName(Constants.CDS_SUBJECTS);
    jsonGenerator.writeStartObject();
    for (CdsSubject subject : cdsSubjects) {
      jsonGenerator.writeFieldName(subject.getCreditorInstitutionCode());
      objectMapper.writeValue(jsonGenerator, subject);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CDS_SUBJECTS - " + cdsSubjects.size() + " items");
  }

  private void writeCdsSubjectServicesToJson(JsonGenerator jsonGenerator) throws IOException {
    List<CdsSubjectService> cdsSubjectServices = getCdsSubjectServices();
    jsonGenerator.writeFieldName(Constants.CDS_SUBJECT_SERVICES);
    jsonGenerator.writeStartObject();
    for (CdsSubjectService service : cdsSubjectServices) {
      jsonGenerator.writeFieldName(service.getSubjectServiceId());
      objectMapper.writeValue(jsonGenerator, service);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CDS_SUBJECT_SERVICES - " + cdsSubjectServices.size() + " items");
  }

  private void writeGdeConfigurationToJson(JsonGenerator jsonGenerator) throws IOException {
    List<GdeConfiguration> gde = getGdeConfiguration();
    jsonGenerator.writeFieldName(Constants.GDE_CONFIGURATIONS);
    jsonGenerator.writeStartObject();
    for (GdeConfiguration config : gde) {
      jsonGenerator.writeFieldName(config.getIdentifier());
      objectMapper.writeValue(jsonGenerator, config);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: GDE_CONFIGURATIONS - " + gde.size() + " items");
  }

  private void writeMetadataDictToJson(JsonGenerator jsonGenerator) throws IOException {
    List<MetadataDict> meta = getMetadataDict();
    jsonGenerator.writeFieldName(Constants.METADATA_DICT);
    jsonGenerator.writeStartObject();
    for (MetadataDict m : meta) {
      jsonGenerator.writeFieldName(m.getKey());
      objectMapper.writeValue(jsonGenerator, m);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: METADATA_DICT - " + meta.size() + " items");
  }

  private void writeConfigurationKeysToJson(JsonGenerator jsonGenerator) throws IOException {
    List<ConfigurationKey> configurationKeyList = getConfigurationKeys();
    jsonGenerator.writeFieldName(Constants.CONFIGURATIONS);
    jsonGenerator.writeStartObject();
    for (ConfigurationKey config : configurationKeyList) {
      jsonGenerator.writeFieldName(config.getIdentifier());
      objectMapper.writeValue(jsonGenerator, config);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CONFIGURATIONS - " + configurationKeyList.size() + " items");
  }

  private void writeFtpServersToJson(JsonGenerator jsonGenerator) throws IOException {
    List<FtpServer> ftpservers = getFtpServers();
    jsonGenerator.writeFieldName(Constants.FTP_SERVERS);
    jsonGenerator.writeStartObject();
    for (FtpServer server : ftpservers) {
      jsonGenerator.writeFieldName(server.getId().toString());
      objectMapper.writeValue(jsonGenerator, server);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: FTP_SERVERS - " + ftpservers.size() + " items");
  }

  private void writeLanguagesToJson(JsonGenerator jsonGenerator) throws IOException {
    jsonGenerator.writeFieldName(Constants.LANGUAGES);
    jsonGenerator.writeStartObject();
    jsonGenerator.writeFieldName("IT");
    jsonGenerator.writeString("IT");
    jsonGenerator.writeFieldName("DE");
    jsonGenerator.writeString("DE");
    jsonGenerator.writeEndObject();
    log.debug("Batch written: LANGUAGES");
  }

  private void writePluginsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<Plugin> plugins = getWfespPluginConfigurations();
    jsonGenerator.writeFieldName(Constants.PLUGINS);
    jsonGenerator.writeStartObject();
    for (Plugin plugin : plugins) {
      jsonGenerator.writeFieldName(plugin.getIdServPlugin());
      objectMapper.writeValue(jsonGenerator, plugin);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: PLUGINS - " + plugins.size() + " items");
  }

  private void writePaymentServiceProvidersToJson(JsonGenerator jsonGenerator) throws IOException {
    List<PaymentServiceProvider> psps = getAllPaymentServiceProviders();
    jsonGenerator.writeFieldName(Constants.PSPS);
    jsonGenerator.writeStartObject();
    for (PaymentServiceProvider psp : psps) {
      jsonGenerator.writeFieldName(psp.getPspCode());
      objectMapper.writeValue(jsonGenerator, psp);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: PSPS - " + psps.size() + " items");
  }

  private void writeChannelsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<Channel> canali = getAllCanali();
    jsonGenerator.writeFieldName(Constants.CHANNELS);
    jsonGenerator.writeStartObject();
    for (Channel channel : canali) {
      jsonGenerator.writeFieldName(channel.getChannelCode());
      objectMapper.writeValue(jsonGenerator, channel);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CHANNELS - " + canali.size() + " items");
  }

  private void writePaymentTypesToJson(JsonGenerator jsonGenerator) throws IOException {
    List<PaymentType> tipiv = getPaymentTypes();
    jsonGenerator.writeFieldName(Constants.PAYMENT_TYPES);
    jsonGenerator.writeStartObject();
    for (PaymentType type : tipiv) {
      jsonGenerator.writeFieldName(type.getPaymentTypeCode());
      objectMapper.writeValue(jsonGenerator, type);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: PAYMENT_TYPES - " + tipiv.size() + " items");
  }

  private void writePspChannelPaymentTypesToJson(JsonGenerator jsonGenerator) throws IOException {
    List<PspChannelPaymentType> pspChannels = getPaymentServiceProvidersChannels();
    jsonGenerator.writeFieldName(Constants.PSP_CHANNEL_PAYMENT_TYPES);
    jsonGenerator.writeStartObject();
    for (PspChannelPaymentType channel : pspChannels) {
      jsonGenerator.writeFieldName(channel.getIdentifier());
      objectMapper.writeValue(jsonGenerator, channel);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: PSP_CHANNEL_PAYMENT_TYPES - " + pspChannels.size() + " items");
  }

  private void writeCreditorInstitutionsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<CreditorInstitution> pas = getCreditorInstitutions();
    jsonGenerator.writeFieldName(Constants.CREDITOR_INSTITUTIONS);
    jsonGenerator.writeStartObject();
    for (CreditorInstitution pa : pas) {
      jsonGenerator.writeFieldName(pa.getCreditorInstitutionCode());
      objectMapper.writeValue(jsonGenerator, pa);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CREDITOR_INSTITUTIONS - " + pas.size() + " items");
  }

  private void writeEncodingsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<Encoding> encodings = getEncodings();
    jsonGenerator.writeFieldName(Constants.ENCODINGS);
    jsonGenerator.writeStartObject();
    for (Encoding encoding : encodings) {
      jsonGenerator.writeFieldName(encoding.getCodeType());
      objectMapper.writeValue(jsonGenerator, encoding);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: ENCODINGS - " + encodings.size() + " items");
  }

  private void writeCreditorInstitutionEncodingsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<CreditorInstitutionEncoding> ciencodings = getCreditorInstitutionEncodings();
    jsonGenerator.writeFieldName(Constants.CREDITOR_INSTITUTION_ENCODINGS);
    jsonGenerator.writeStartObject();
    for (CreditorInstitutionEncoding encoding : ciencodings) {
      jsonGenerator.writeFieldName(encoding.getIdentifier());
      objectMapper.writeValue(jsonGenerator, encoding);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CREDITOR_INSTITUTION_ENCODINGS - " + ciencodings.size() + " items");
  }

  private void writeStationCreditorInstitutionsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<StationCreditorInstitution> paspa = findAllPaStazioniPa();
    jsonGenerator.writeFieldName(Constants.CREDITOR_INSTITUTION_STATIONS);
    jsonGenerator.writeStartObject();
    for (StationCreditorInstitution station : paspa) {
      jsonGenerator.writeFieldName(station.getIdentifier());
      objectMapper.writeValue(jsonGenerator, station);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CREDITOR_INSTITUTION_STATIONS - " + paspa.size() + " items");
  }

  private void writeMaintenanceStationsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<MaintenanceStation> maintenanceStations = findAllStationMaintenance();
    jsonGenerator.writeFieldName(Constants.MAINTENANCE_STATIONS);
    jsonGenerator.writeStartObject();
    for (MaintenanceStation station : maintenanceStations) {
      jsonGenerator.writeFieldName(station.getStationCode());
      objectMapper.writeValue(jsonGenerator, station);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: MAINTENANCE_STATIONS - " + maintenanceStations.size() + " items");
  }

  private void writeStationsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<Station> stazioni = findAllStazioni();
    jsonGenerator.writeFieldName(Constants.STATIONS);
    jsonGenerator.writeStartObject();
    for (Station station : stazioni) {
      jsonGenerator.writeFieldName(station.getStationCode());
      objectMapper.writeValue(jsonGenerator, station);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: STATIONS - " + stazioni.size() + " items");
  }

  private void writeIbansToJson(JsonGenerator jsonGenerator) throws IOException {
    List<Iban> ibans = getCurrentIbans();
    jsonGenerator.writeFieldName(Constants.IBANS);
    jsonGenerator.writeStartObject();
    for (Iban iban : ibans) {
      jsonGenerator.writeFieldName(iban.getIdentifier());
      objectMapper.writeValue(jsonGenerator, iban);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: IBANS - " + ibans.size() + " items");
  }

  private void writePspInformationsToJson(JsonGenerator jsonGenerator) throws IOException {
    Pair<List<PspInformation>, List<PspInformation>> informativePspAndTemplates =
        getInformativePspAndTemplates();
    List<PspInformation> infopsps = informativePspAndTemplates.getLeft();

    jsonGenerator.writeFieldName(Constants.PSP_INFORMATIONS);
    jsonGenerator.writeStartObject();
    for (PspInformation info : infopsps) {
      jsonGenerator.writeFieldName(info.getPsp());
      objectMapper.writeValue(jsonGenerator, info);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: PSP_INFORMATIONS - " + infopsps.size() + " items");
  }

  private void writePspInformationTemplatesToJson(JsonGenerator jsonGenerator) throws IOException {
    Pair<List<PspInformation>, List<PspInformation>> informativePspAndTemplates =
        getInformativePspAndTemplates();
    List<PspInformation> infopspTemplates = informativePspAndTemplates.getRight();

    jsonGenerator.writeFieldName(Constants.PSP_INFORMATION_TEMPLATES);
    jsonGenerator.writeStartObject();
    for (PspInformation template : infopspTemplates) {
      jsonGenerator.writeFieldName(template.getPsp());
      objectMapper.writeValue(jsonGenerator, template);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: PSP_INFORMATION_TEMPLATES - " + infopspTemplates.size() + " items");
  }

  private void writeCreditorInstitutionInformationsToJson(JsonGenerator jsonGenerator) throws IOException {
    List<CreditorInstitutionInformation> infopas = getInformativePa();
    jsonGenerator.writeFieldName(Constants.CREDITOR_INSTITUTION_INFORMATIONS);
    jsonGenerator.writeStartObject();
    for (CreditorInstitutionInformation info : infopas) {
      jsonGenerator.writeFieldName(info.getPa());
      objectMapper.writeValue(jsonGenerator, info);
    }
    jsonGenerator.writeEndObject();
    log.debug("Batch written: CREDITOR_INSTITUTION_INFORMATIONS - " + infopas.size() + " items");
  }


  public void sendEvent(String id, ZonedDateTime now) {
    if(SEND_EVENT){
        try {
            eventHubService.publishEvent(id,now,getVersion());
        } catch (JsonProcessingException e) {
            throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
        }
    }
  }

  private String getVersion() {
    String version = Constants.GZIP_JSON + "-" + APP_VERSION;
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
    redisRepository.save(actualKeyV1, "1".getBytes(StandardCharsets.UTF_8), IN_PROGRESS_TTL);
  }

  public Boolean getCacheInProgress() {
    String actualKeyV1 = cacheKeyUtils.getCacheKeyInProgress(Constants.FULL);
    return redisRepository.getBooleanByKeyId(actualKeyV1);
  }

  public CacheVersion getCacheId() {
    String actualKeyV1 = cacheKeyUtils.getCacheIdKey(Constants.FULL);
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
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMAM_INSTANCE);
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
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMAM_INSTANCE);
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
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, SCHEMAM_INSTANCE);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.marshal(informativaPA, baos);
      return Constants.ENCODER.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      log.error("error creating CtListaInformativeControparte", e);
      return e.toString();
    }
  }

  public Pair<List<PspInformation>, List<PspInformation>> getInformativePspAndTemplates() {
//    List<Psp> psps = pspRepository.findAll();
//    List<CdiPreference> preferences = cdiPreferenceRepository.findAll();
//    List<CdiFasciaCostoServizio> allFasce = cdiFasceRepository.findAll();
    List<CdiMasterValid> masters =
        StreamSupport.stream(cdiMasterValidRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
//    List<CdiDetail> details = cdiDetailRepository.findAll();
//    List<CdiInformazioniServizio> allInformazioni = cdiInformazioniServizioRepository.findAll();

    List<PspInformation> informativePsp =
        new ArrayList<>(); // getInformativePsp(psps, masters, details, preferences, allFasce,
    // allInformazioni);
    List<PspInformation> templateInformativePsp = getTemplateInformativePsp(masters);

    return Pair.of(informativePsp, templateInformativePsp);
  }

//  public List<PspInformation> getInformativePsp(
//      List<Psp> psps,
//      List<CdiMasterValid> masters,
//      List<CdiDetail> details,
//      List<CdiPreference> preferences,
//      List<CdiFasciaCostoServizio> allFasce,
//      List<CdiInformazioniServizio> allInformazioni) {
//    log.info("loading InformativePsp");
//
//    List<CtListaInformativePSP> informativePspSingle =
//        masters
//            .stream()
//            .filter(
//                m -> details.stream().anyMatch(d -> d.getFkCdiMaster().getId().equals(m.getId())))
//            .map(
//                cdiMaster -> {
//                  Psp psp =
//                      psps.stream()
//                          .filter(p -> p.getObjId().equals(cdiMaster.getFkPsp().getObjId()))
//                          .findFirst()
//                          .get();
//                  CtInformativaPSP ctInformativaPSP = new CtInformativaPSP();
//                  ctInformativaPSP.setCodiceABI(psp.getAbi());
//                  ctInformativaPSP.setCodiceBIC(psp.getBic());
//                  ctInformativaPSP.setIdentificativoPSP(psp.getIdPsp());
//                  ctInformativaPSP.setRagioneSociale(psp.getRagioneSociale());
//                  CtInformativaMaster ctInformativaMaster = new CtInformativaMaster();
//                  try {
//                    ctInformativaMaster.setDataInizioValidita(
//                        tsToXmlGC(cdiMaster.getDataInizioValidita()));
//                  } catch (DatatypeConfigurationException e) {
//                    throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
//                  }
//                  try {
//                    ctInformativaMaster.setDataPubblicazione(
//                        tsToXmlGC(cdiMaster.getDataPubblicazione()));
//                  } catch (DatatypeConfigurationException e) {
//                    throw new AppException(AppError.INTERNAL_SERVER_ERROR, e);
//                  }
//                  ctInformativaMaster.setLogoPSP("".getBytes(StandardCharsets.UTF_8));
//                  ctInformativaMaster.setStornoPagamento(
//                      Boolean.TRUE.equals(cdiMaster.getStornoPagamento()) ? 1 : 0);
//                  ctInformativaMaster.setUrlInformazioniPSP(cdiMaster.getUrlInformazioniPsp());
//                  ctInformativaMaster.setMarcaBolloDigitale(
//                      Boolean.TRUE.equals(cdiMaster.getMarcaBolloDigitale()) ? 1 : 0);
//                  ctInformativaPSP.setInformativaMaster(ctInformativaMaster);
//                  ctInformativaPSP.setIdentificativoFlusso(cdiMaster.getIdInformativaPsp());
//
//                  List<CtInformativaDetail> masterdetails =
//                      details
//                          .stream()
//                          .filter(d -> d.getFkCdiMaster().getId().equals(cdiMaster.getId()))
//                          .filter(
//                              d ->
//                                  !d.getPspCanaleTipoVersamento()
//                                      .getCanaleTipoVersamento()
//                                      .getTipoVersamento()
//                                      .equals("PPAY"))
//                          .map(
//                              cdiDetail -> {
//                                var pspCanaleTipoVersamento =
//                                    cdiDetail.getPspCanaleTipoVersamento();
//
//                                CtIdentificazioneServizio ctIdentificazioneServizio =
//                                    new CtIdentificazioneServizio();
//                                ctIdentificazioneServizio.setNomeServizio(
//                                    cdiDetail.getNomeServizio());
//                                ctIdentificazioneServizio.setLogoServizio(
//                                    "".getBytes(StandardCharsets.UTF_8));
//
//                                List<CdiInformazioniServizio> it =
//                                    allInformazioni
//                                        .stream()
//                                        .filter(
//                                            ii ->
//                                                ii.getFkCdiDetail()
//                                                    .getId()
//                                                    .equals(cdiDetail.getId()))
//                                        .filter(info -> info.getCodiceLingua().equals("IT"))
//                                        .collect(Collectors.toList());
//                                CtListaInformazioniServizio ctListaInformazioniServizio =
//                                    new CtListaInformazioniServizio();
//                                if (!it.isEmpty()) {
//                                  CtInformazioniServizio ctInformazioniServizio =
//                                      new CtInformazioniServizio();
//                                  ctInformazioniServizio.setDescrizioneServizio(
//                                      it.get(0).getDescrizioneServizio());
//                                  ctInformazioniServizio.setCodiceLingua(
//                                          StCodiceLingua.fromValue(it.get(0).getCodiceLingua()));
//                                  ctInformazioniServizio.setDisponibilitaServizio(
//                                      it.get(0).getDisponibilitaServizio());
//                                  ctInformazioniServizio.setUrlInformazioniCanale(
//                                      it.get(0).getUrlInformazioniCanale());
//                                  ctListaInformazioniServizio
//                                      .getInformazioniServizio()
//                                      .add(ctInformazioniServizio);
//                                }
//
//                                List<CtFasciaCostoServizio> fasce =
//                                    allFasce
//                                        .stream()
//                                        .filter(
//                                            fas ->
//                                                fas.getFkCdiDetail()
//                                                    .getId()
//                                                    .equals(cdiDetail.getId()))
//                                        .map(
//                                            fascia -> {
//                                              CtFasciaCostoServizio ctFasciaCostoServizio =
//                                                  new CtFasciaCostoServizio();
//                                              ctFasciaCostoServizio.setCostoFisso(
//                                                  BigDecimal.valueOf(fascia.getCostoFisso())
//                                                      .setScale(2, RoundingMode.FLOOR));
//                                              ctFasciaCostoServizio.setImportoMassimoFascia(
//                                                  BigDecimal.valueOf(fascia.getImportoMassimo())
//                                                      .setScale(2, RoundingMode.FLOOR));
//                                              ctFasciaCostoServizio.setValoreCommissione(
//                                                  (BigDecimal.valueOf(fascia.getValoreCommissione())
//                                                      .setScale(2, RoundingMode.FLOOR)));
//                                              return ctFasciaCostoServizio;
//                                            })
//                                        .collect(Collectors.toList());
//                                CtListaFasceCostoServizio ctListaFasceCostoServizio =
//                                    new CtListaFasceCostoServizio();
//                                ctListaFasceCostoServizio.getFasciaCostoServizio().addAll(fasce);
//
//                                List<CdiPreference> cdiPreferenceStream =
//                                    preferences
//                                        .stream()
//                                        .filter(
//                                            pref ->
//                                                pref.getCdiDetail()
//                                                    .getId()
//                                                    .equals(cdiDetail.getId()))
//                                        .collect(Collectors.toList());
//                                List<String> buyers =
//                                    cdiPreferenceStream
//                                        .stream()
//                                        .map(p -> p.getBuyer())
//                                        .collect(Collectors.toList());
//                                CtListaConvenzioni listaConvenzioni = new CtListaConvenzioni();
//                                listaConvenzioni.getCodiceConvenzione().addAll(buyers);
//
//                                CtInformativaDetail ctInformativaDetail = new CtInformativaDetail();
//                                ctInformativaDetail.setCanaleApp(
//                                    cdiDetail.getCanaleApp().intValue());
//                                ctInformativaDetail.setIdentificativoCanale(
//                                    pspCanaleTipoVersamento
//                                        .getCanaleTipoVersamento()
//                                        .getCanale()
//                                        .getIdCanale());
//
//                                List<Double> costiConvenzione =
//                                    cdiPreferenceStream
//                                        .stream()
//                                        .map(p -> p.getCostoConvenzione() / COSTO_CONVENZIONE_FORMAT)
//                                        .collect(Collectors.toList());
//
//                                CtCostiServizio costiServizio = new CtCostiServizio();
//                                costiServizio.setTipoCostoTransazione(1);
//                                costiServizio.setTipoCommissione(0);
//                                costiServizio.setListaFasceCostoServizio(ctListaFasceCostoServizio);
//                                if (!costiConvenzione.isEmpty()) {
//                                  costiServizio.setCostoConvenzione(
//                                      BigDecimal.valueOf(costiConvenzione.get(0)));
//                                }
//                                ctInformativaDetail.setCostiServizio(costiServizio);
//
//                                ctInformativaDetail.setPriorita(cdiDetail.getPriorita().intValue());
//                                ctInformativaDetail.setListaConvenzioni(listaConvenzioni);
//                                ctInformativaDetail.setIdentificativoIntermediario(
//                                    pspCanaleTipoVersamento
//                                        .getCanaleTipoVersamento()
//                                        .getCanale()
//                                        .getFkIntermediarioPsp()
//                                        .getIdIntermediarioPsp());
//                                ctInformativaDetail.setIdentificazioneServizio(
//                                    ctIdentificazioneServizio);
//                                ctInformativaDetail.setListaInformazioniServizio(
//                                    ctListaInformazioniServizio);
//                                if (cdiDetail.getTags() != null) {
//                                  CtListaParoleChiave ctListaParoleChiave =
//                                      new CtListaParoleChiave();
//                                  ctListaParoleChiave
//                                      .getParoleChiave()
//                                      .addAll(
//                                          Arrays.stream(cdiDetail.getTags().split(";"))
//                                              .map(StParoleChiave::fromValue)
//                                              .collect(Collectors.toList()));
//                                  ctInformativaDetail.setListaParoleChiave(ctListaParoleChiave);
//                                }
//                                ctInformativaDetail.setModelloPagamento(
//                                    cdiDetail.getModelloPagamento().intValue());
//                                ctInformativaDetail.setTipoVersamento(
//                                    StTipoVersamento.fromValue(
//                                        pspCanaleTipoVersamento
//                                            .getCanaleTipoVersamento()
//                                            .getTipoVersamento()
//                                            .getTipoVersamento()));
//                                return ctInformativaDetail;
//                              })
//                          .collect(Collectors.toList());
//                  CtListaInformativaDetail listaInformativaDetail = new CtListaInformativaDetail();
//                  listaInformativaDetail.getInformativaDetail().addAll(masterdetails);
//                  ctInformativaPSP.setListaInformativaDetail(listaInformativaDetail);
//
//                  CtListaInformativePSP ctListaInformativePSP = new CtListaInformativePSP();
//                  ctListaInformativePSP.getInformativaPSP().add(ctInformativaPSP);
//                  return ctListaInformativePSP;
//                })
//            .collect(Collectors.toList());
//
//    CtListaInformativePSP informativaPspFull = new CtListaInformativePSP();
//    informativePspSingle.forEach(
//        i -> informativaPspFull.getInformativaPSP().addAll(i.getInformativaPSP()));
//
//    CtListaInformativePSP informativaEmpty = new CtListaInformativePSP();
//
//    List<PspInformation> informativePspSingleCache =
//        informativePspSingle
//            .stream()
//            .map(
//                i ->
//                    PspInformation.builder()
//                        .psp(i.getInformativaPSP().get(0).getIdentificativoPSP())
//                        .informativa(toXml(i))
//                        .build())
//            .collect(Collectors.toList());
//
//    PspInformation informativaPSPFull =
//        PspInformation.builder().psp(Constants.FULL_INFORMATION).informativa(toXml(informativaPspFull)).build();
//
//    PspInformation informativaPSPEmpty =
//        PspInformation.builder().psp("EMPTY").informativa(toXml(informativaEmpty)).build();
//
//    informativePspSingleCache.add(informativaPSPFull);
//    informativePspSingleCache.add(informativaPSPEmpty);
//    return informativePspSingleCache;
//  }

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
