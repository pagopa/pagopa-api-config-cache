package it.gov.pagopa.apiconfig.service;


import it.gov.pagopa.apiconfig.catalogodati.CtCostiServizio;
import it.gov.pagopa.apiconfig.catalogodati.CtFasciaCostoServizio;
import it.gov.pagopa.apiconfig.catalogodati.CtIdentificazioneServizio;
import it.gov.pagopa.apiconfig.catalogodati.CtInformativaDetail;
import it.gov.pagopa.apiconfig.catalogodati.CtInformativaMaster;
import it.gov.pagopa.apiconfig.catalogodati.CtInformativaPSP;
import it.gov.pagopa.apiconfig.catalogodati.CtInformazioniServizio;
import it.gov.pagopa.apiconfig.catalogodati.CtListaConvenzioni;
import it.gov.pagopa.apiconfig.catalogodati.CtListaFasceCostoServizio;
import it.gov.pagopa.apiconfig.catalogodati.CtListaInformativaDetail;
import it.gov.pagopa.apiconfig.catalogodati.CtListaInformativePSP;
import it.gov.pagopa.apiconfig.catalogodati.CtListaInformazioniServizio;
import it.gov.pagopa.apiconfig.catalogodati.CtListaParoleChiave;
import it.gov.pagopa.apiconfig.catalogodati.StCodiceLingua;
import it.gov.pagopa.apiconfig.catalogodati.StParoleChiave;
import it.gov.pagopa.apiconfig.catalogodati.StTipoVersamento;
import it.gov.pagopa.apiconfig.controparti.CtContoAccredito;
import it.gov.pagopa.apiconfig.controparti.CtErogazione;
import it.gov.pagopa.apiconfig.controparti.CtErogazioneServizio;
import it.gov.pagopa.apiconfig.controparti.CtFasciaOraria;
import it.gov.pagopa.apiconfig.controparti.CtInformativaControparte;
import it.gov.pagopa.apiconfig.controparti.CtListaInformativeControparte;
import it.gov.pagopa.apiconfig.controparti.StTipoPeriodo;
import it.gov.pagopa.apiconfig.entity.CdiInformazioniServizio;
import it.gov.pagopa.apiconfig.entity.CdiMasterValid;
import it.gov.pagopa.apiconfig.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.entity.InformativePaDetail;
import it.gov.pagopa.apiconfig.entity.InformativePaMaster;
import it.gov.pagopa.apiconfig.entity.Pa;
import it.gov.pagopa.apiconfig.entity.Psp;
import it.gov.pagopa.apiconfig.entity.PspCanaleTipoVersamentoCanale;
import it.gov.pagopa.apiconfig.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.model.node.v1.cds.CdsCategory;
import it.gov.pagopa.apiconfig.model.node.v1.cds.CdsService;
import it.gov.pagopa.apiconfig.model.node.v1.cds.CdsSubject;
import it.gov.pagopa.apiconfig.model.node.v1.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.FtpServer;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.GdeConfiguration;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.MetadataDict;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.PaymentType;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.Plugin;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.CreditorInstitutionInformation;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.StationCreditorInstitution;
import it.gov.pagopa.apiconfig.model.node.v1.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.model.node.v1.psp.Channel;
import it.gov.pagopa.apiconfig.model.node.v1.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.model.node.v1.psp.PspChannelPaymentType;
import it.gov.pagopa.apiconfig.model.node.v1.psp.PspInformation;
import it.gov.pagopa.apiconfig.redis.RedisRepository;
import it.gov.pagopa.apiconfig.repository.CanaliRepository;
import it.gov.pagopa.apiconfig.repository.CdiDetailRepository;
import it.gov.pagopa.apiconfig.repository.CdiFasciaCostoServizioRepository;
import it.gov.pagopa.apiconfig.repository.CdiMasterValidRepository;
import it.gov.pagopa.apiconfig.repository.CdiPreferenceRepository;
import it.gov.pagopa.apiconfig.repository.CdsCategorieRepository;
import it.gov.pagopa.apiconfig.repository.CdsServizioRepository;
import it.gov.pagopa.apiconfig.repository.CdsSoggettoRepository;
import it.gov.pagopa.apiconfig.repository.CdsSoggettoServizioRepository;
import it.gov.pagopa.apiconfig.repository.CodifichePaRepository;
import it.gov.pagopa.apiconfig.repository.CodificheRepository;
import it.gov.pagopa.apiconfig.repository.ConfigurationKeysRepository;
import it.gov.pagopa.apiconfig.repository.DizionarioMetadatiRepository;
import it.gov.pagopa.apiconfig.repository.FtpServersRepository;
import it.gov.pagopa.apiconfig.repository.GdeConfigRepository;
import it.gov.pagopa.apiconfig.repository.IbanValidiPerPaRepository;
import it.gov.pagopa.apiconfig.repository.InformativePaDetailRepository;
import it.gov.pagopa.apiconfig.repository.InformativePaMasterRepository;
import it.gov.pagopa.apiconfig.repository.IntermediariPaRepository;
import it.gov.pagopa.apiconfig.repository.IntermediariPspRepository;
import it.gov.pagopa.apiconfig.repository.PaRepository;
import it.gov.pagopa.apiconfig.repository.PaStazionePaRepository;
import it.gov.pagopa.apiconfig.repository.PspCanaleTipoVersamentoRepository;
import it.gov.pagopa.apiconfig.repository.PspRepository;
import it.gov.pagopa.apiconfig.repository.StazioniRepository;
import it.gov.pagopa.apiconfig.repository.TipiVersamentoRepository;
import it.gov.pagopa.apiconfig.repository.WfespPluginConfRepository;
import it.gov.pagopa.apiconfig.template.TplCostiServizio;
import it.gov.pagopa.apiconfig.template.TplFasciaCostoServizio;
import it.gov.pagopa.apiconfig.template.TplIdentificazioneServizio;
import it.gov.pagopa.apiconfig.template.TplInformativaDetail;
import it.gov.pagopa.apiconfig.template.TplInformativaMaster;
import it.gov.pagopa.apiconfig.template.TplInformativaPSP;
import it.gov.pagopa.apiconfig.template.TplInformazioniServizio;
import it.gov.pagopa.apiconfig.template.TplListaFasceCostoServizio;
import it.gov.pagopa.apiconfig.template.TplListaInformativaDetail;
import it.gov.pagopa.apiconfig.template.TplListaInformazioniServizio;
import it.gov.pagopa.apiconfig.template.TplListaParoleChiave;
import it.gov.pagopa.apiconfig.util.ConfigMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Service
@Transactional
public class ConfigService {

  @Autowired
  private PlatformTransactionManager transactionManager;
  @Autowired
  private RedisRepository redisRepository;
  @Autowired
  private ConfigMapper modelMapper;
  @Autowired
  private ConfigurationKeysRepository configurationKeysRepository;
  @Autowired
  private IntermediariPaRepository intermediariPaRepository;
  @Autowired
  private IntermediariPspRepository intermediariPspRepository;
  @Autowired
  private CdsCategorieRepository cdsCategorieRepository;
  @Autowired
  private CdsSoggettoRepository cdsSoggettoRepository;
  @Autowired
  private CdsServizioRepository cdsServizioRepository;
  @Autowired
  private CdsSoggettoServizioRepository cdsSoggettoServizioRepository;
  @Autowired
  private GdeConfigRepository gdeConfigRepository;
  @Autowired
  private DizionarioMetadatiRepository dizionarioMetadatiRepository;
  @Autowired
  private FtpServersRepository ftpServersRepository;
  @Autowired
  private TipiVersamentoRepository tipiVersamentoRepository;
  @Autowired
  private WfespPluginConfRepository wfespPluginConfRepository;
  @Autowired
  private CodifichePaRepository codifichePaRepository;
  @Autowired
  private CodificheRepository codificheRepository;
  @Autowired
  private IbanValidiPerPaRepository ibanValidiPerPaRepository;
  @Autowired
  private StazioniRepository stazioniRepository;
  @Autowired
  private PaStazionePaRepository paStazioniRepository;
  @Autowired
  private PaRepository paRepository;
  @Autowired
  private CanaliRepository canaliRepository;
  @Autowired
  private PspCanaleTipoVersamentoRepository pspCanaleTipoVersamentoRepository;
  @Autowired
  private PspRepository pspRepository;
  @Autowired
  private CdiMasterValidRepository cdiMasterValidRepository;
  @Autowired
  private CdiDetailRepository cdiDetailRepository;
  @Autowired
  private CdiPreferenceRepository cdiPreferenceRepository;
  @Autowired
  private CdiFasciaCostoServizioRepository cdiFasceRepository;
  @Autowired
  private InformativePaMasterRepository informativePaMasterRepository;
  @Autowired
  private InformativePaDetailRepository informativePaDetailRepository;

  private String DA_COMPILARE_FLUSSO = "DA COMPILARE (formato: [IDPSP]_dd-mm-yyyy - esempio: ESEMPIO_31-12-2001)";
  private String DA_COMPILARE = "DA COMPILARE";
  private String KEY_V1 = "node_v1";
  private String KEY_V1_VERSION = "node_v1_version";

  @PostConstruct
  private void initAllCaches() throws IOException {

    new TransactionTemplate(transactionManager).execute(new TransactionCallback(){

      @Override
      public Object doInTransaction(TransactionStatus transactionStatus) {

        try {
          newCacheV1();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        return null;
      }

    });
  }

  public ConfigDataV1 newCacheV1() throws IOException {

    long startTime = System.nanoTime();

    ConfigDataV1 configData = new ConfigDataV1();


    List<BrokerCreditorInstitution> intpa = getBrokerDetails();
    Map intpamap = new HashMap<String, BrokerCreditorInstitution>();
    intpa.stream().forEach(k->{
      intpamap.put(k.getBrokerCode(),k);
    });
    configData.setIntermediariPa(intpamap);

    List<BrokerPsp> intpsp = getBrokerPspDetails();
    Map intpspmap = new HashMap<String, BrokerPsp>();
    intpsp.stream().forEach(k->{
      intpspmap.put(k.getBrokerPspCode(),k);
    });
    configData.setIntermediariPsp(intpspmap);

    List<CdsCategory> cdscats = getCdsCategories();
    Map cdscatsMap = new HashMap<String, CdsCategory>();
    cdscats.stream().forEach(k->cdscatsMap.put(k.getDescription(),k));
    configData.setCdsCategorie(cdscatsMap);

    List<CdsService> cdsServices = getCdsServices();
    Map cdsServicesMap = new HashMap<String, CdsService>();
    cdsServices.stream().forEach(k->cdsServicesMap.put(k.getIdentifier(),k));
    configData.setCdsServizi(cdsServicesMap);

    List<CdsSubject> cdsSubjects = getCdsSubjects();
    Map cdsSubjectsMap = new HashMap<String, CdsSubject>();
    cdsSubjects.stream().forEach(k->cdsSubjectsMap.put(k.getCreditorInstitutionCode(),k));
    configData.setCdsSoggetti(cdsSubjectsMap);

    List<CdsSubjectService> cdsSubjectServices = getCdsSubjectServices();
    Map cdsSubjectServicesMap = new HashMap<String,CdsSubjectService>();
    cdsSubjectServices.stream().forEach(k->cdsSubjectServicesMap.put(k.getSubjectServiceId(),k));
    configData.setCdsSoggettiServizi(cdsSubjectServicesMap);

    List<GdeConfiguration> gde = getGdeConfiguration();
    Map gdeMap = new HashMap<String, GdeConfiguration>();
    gde.stream().forEach(k->{
      gdeMap.put(k.getIdentifier(),k);
    });
    configData.setConfigurazioniGde(gdeMap);

    List<MetadataDict> meta = getMetadataDict();
    Map metaMap = new HashMap<String,MetadataDict>();
    meta.stream().forEach(k->{
      metaMap.put(k.getKey(),k);
    });
    configData.setDizionarioMetadati(metaMap);

    List<ConfigurationKey> configurationKeyList = getConfigurationKeys();
    Map configMap = new HashMap<String,ConfigurationKey>();
    configurationKeyList.stream().forEach(k->{
      configMap.put(k.getIdentifier(),k);
    });
    configData.setConfigurazioni(configMap);

    List<FtpServer> ftpservers = getFtpServers();
    Map ftpserversMap = new HashMap<String,FtpServer>();
    ftpservers.stream().forEach(k->{
      ftpserversMap.put(k.getIdentifier(),k);
    });
    configData.setFtpServers(ftpserversMap);

    Map codiciLingua = new HashMap<String,String>();
    codiciLingua.put("IT","IT");
    codiciLingua.put("DE","DE");
    configData.setCodiciLingua(codiciLingua);

    List<Plugin> plugins = getWfespPluginConfigurations();
    Map pluginsMap = new HashMap<String,Plugin>();
    plugins.stream().forEach(k->{
      pluginsMap.put(k.getIdServPlugin(),k);
    });
    configData.setPlugins(pluginsMap);

    List<PaymentServiceProvider> psps = getAllPaymentServiceProviders();
    Map pspMap = new HashMap<String, PaymentServiceProvider>();
    psps.stream().forEach(k->{
      pspMap.put(k.getPspCode(),k);
    });
    configData.setPsps(pspMap);

    List<Channel> canali = getAllCanali();
    Map canalimap = new HashMap<String, Channel>();
    canali.stream().forEach(k->{
      canalimap.put(k.getChannelCode(),k);
    });
    configData.setCanali(canalimap);
    List<PaymentType> tipiv = getPaymentTypes();
    Map tipivMap = new HashMap<String, PaymentType>();
    tipiv.stream().forEach(k->{
      tipivMap.put(k.getPaymentTypeCode(),k);
    });

    configData.setTipiVersamento(tipivMap);
    List<PspChannelPaymentType> pspChannels = getPaymentServiceProvidersChannels();
    Map pspChannelsMap = new HashMap<String, PspChannelPaymentType>();
    pspChannels.stream().forEach(k->{
      pspChannelsMap.put(k.getIdentifier(),k);
    });
    configData.setPspCanaliTipiVersamento(pspChannelsMap);

    List<CreditorInstitution> pas = getCreditorInstitutions();
    Map pamap = new HashMap<String, CreditorInstitution>();
    pas.stream().forEach(k->{
      pamap.put(k.getCreditorInstitutionCode(),k);
    });
    configData.setPas(pamap);

    List<Encoding> encodings = getEncodings();
    Map encodingsMap = new HashMap<String,Encoding>();
    encodings.stream().forEach(k->{
      encodingsMap.put(k.getCodeType(),k);
    });
    configData.setCodifiche(encodingsMap);

    List<CreditorInstitutionEncoding> ciencodings = getCreditorInstitutionEncodings();
    Map ciencodingsMap = new HashMap<String, CreditorInstitutionEncoding>();
    ciencodings.stream().forEach(k->{
      ciencodingsMap.put(k.getIdentifier(),k);
    });
    configData.setCodifichePa(ciencodingsMap);

    List<StationCreditorInstitution> paspa = findAllPaStazioniPa();
    Map paspamap = new HashMap<String, StationCreditorInstitution>();
    paspa.stream().forEach(k->{
      paspamap.put(k.getIdentifier(),k);
    });
    configData.setStazioniPa(paspamap);

    List<Station> stazioni = findAllStazioni();
    Map stazionimap = new HashMap<String, Station>();
    stazioni.stream().forEach(k->{
      stazionimap.put(k.getStationCode(),k);
    });
    configData.setStazioni(stazionimap);
    List<Iban> ibans = getCurrentIbans();
    Map ibansMap = new HashMap<String,Encoding>();
    ibans.stream().forEach(k->{
      ibansMap.put(k.getIdentifier(),k);
    });
    configData.setIbans(ibansMap);

    List<PspInformation> infopsps = getInformativePsp();
    Map infopspsMap = new HashMap<String, PspInformation>();
    infopsps.stream().forEach(k->{
      infopspsMap.put(k.getPsp(),k);
    });
    configData.setInformativeCdi(infopspsMap);

    List<PspInformation> infopspTemplates = getTemplateInformativePsp();
    Map infopspTemplatesMap = new HashMap<String, PspInformation>();
    infopspTemplates.stream().forEach(k->{
      infopspTemplatesMap.put(k.getPsp(),k);
    });
    configData.setInformativeCdiTemplate(infopspTemplatesMap);

    List<CreditorInstitutionInformation> infopas = getInformativePa();
    Map infopasMap = new HashMap<String,CreditorInstitutionInformation>();
    infopas.stream().forEach(k->{
      infopasMap.put(k.getPa(),k);
    });
    configData.setInformativePa(infopasMap);

    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;
    log.info("cache loaded in "+duration+"ms");

    configData.setVersion(""+endTime);

    redisRepository.save(KEY_V1,configData,1440);
    redisRepository.save(KEY_V1_VERSION,configData.getVersion(),1440);

    return configData;
  }

  public String getCacheV1Version(){
    Object v = redisRepository.get(KEY_V1_VERSION);
    if(v!=null){
      return (String)v;
    }else{
      return null;
    }
  }

  public List<ConfigurationKey> getConfigurationKeys() {
    log.info("loading ConfigurationKeys");
    return modelMapper.modelMapper()
        .map(configurationKeysRepository.findAll(), new TypeToken<List<ConfigurationKey>>() {
        }.getType());
  }

  public List<BrokerCreditorInstitution> getBrokerDetails() {
    log.info("loading PaBrokers");
    return modelMapper.modelMapper()
        .map(intermediariPaRepository.findAll(), new TypeToken<List<BrokerCreditorInstitution>>() {
        }.getType());
  }

  public List<BrokerPsp> getBrokerPspDetails() {
    log.info("loading PspBrokers");
    return modelMapper.modelMapper()
        .map(intermediariPspRepository.findAll(), new TypeToken<List<BrokerPsp>>() {
        }.getType());
  }

  public List<CdsCategory> getCdsCategories() {
    log.info("loading CdsCategories");
    return modelMapper.modelMapper()
        .map(cdsCategorieRepository.findAll(), new TypeToken<List<CdsCategory>>() {
        }.getType());
  }

  public List<CdsService> getCdsServices() {
    log.info("loading CdsServices");
    return modelMapper.modelMapper()
        .map(cdsServizioRepository.findAllFetching(), new TypeToken<List<CdsService>>() {
        }.getType());
  }

  public List<CdsSubject> getCdsSubjects() {
    log.info("loading CdsSubjects");
    return modelMapper.modelMapper()
        .map(cdsSoggettoRepository.findAll(), new TypeToken<List<CdsSubject>>() {
        }.getType());
  }

  public List<CdsSubjectService> getCdsSubjectServices() {
    log.info("loading CdsSubjectServices");
    return modelMapper.modelMapper()
        .map(cdsSoggettoServizioRepository.findAllFetching(), new TypeToken<List<CdsSubjectService>>() {
        }.getType());
  }

  public List<GdeConfiguration> getGdeConfiguration() {
    log.info("loading GdeConfigurations");
    return modelMapper.modelMapper()
        .map(gdeConfigRepository.findAll(), new TypeToken<List<GdeConfiguration>>() {
        }.getType());
  }

  public List<MetadataDict> getMetadataDict() {
    log.info("loading MetadataDicts");
    return modelMapper.modelMapper()
        .map(dizionarioMetadatiRepository.findAll(), new TypeToken<List<MetadataDict>>() {
        }.getType());
  }

  public List<FtpServer> getFtpServers() {
    log.info("loading FtpServers");
    return modelMapper.modelMapper()
        .map(ftpServersRepository.findAll(), new TypeToken<List<FtpServer>>() {
        }.getType());
  }

  public List<PaymentType> getPaymentTypes() {
    log.info("loading PaymentTypes");
    return modelMapper.modelMapper()
        .map(tipiVersamentoRepository.findAll(), new TypeToken<List<PaymentType>>() {
        }.getType());
  }

  public List<Plugin> getWfespPluginConfigurations() {
    log.info("loading Plugins");
    return modelMapper.modelMapper()
        .map(wfespPluginConfRepository.findAll(), new TypeToken<List<Plugin>>() {
        }.getType());
  }

  public List<Iban> getCurrentIbans() {
    log.info("loading Ibans");
    return modelMapper.modelMapper()
        .map(ibanValidiPerPaRepository.findAllFetching(), new TypeToken<List<Iban>>() {
        }.getType());
  }

  public List<Station> findAllStazioni() {
    log.info("loading Stations");
    return modelMapper.modelMapper()
        .map(stazioniRepository.findAllFetching(), new TypeToken<List<Station>>() {
        }.getType());
  }

  public List<StationCreditorInstitution> findAllPaStazioniPa() {
    log.info("loading PaStations");
    return paStazioniRepository.findAllFetching().stream().map(s -> {
      return new StationCreditorInstitution(
          s.getPa().getIdDominio(),
          s.getStazione().getIdStazione(),
          s.getProgressivo(),
          s.getAuxDigit(),
          s.getSegregazione(),
          s.getQuartoModello(),
          s.getBroadcast(),
          s.getStazione().getVersionePrimitive(),
          s.getPagamentoSpontaneo());
    }).collect(Collectors.toList());
  }

  public List<CreditorInstitution> getCreditorInstitutions() {
    log.info("loading Pas");
    return modelMapper.modelMapper()
        .map(paRepository.findAll(), new TypeToken<List<CreditorInstitution>>() {
        }.getType());
  }

  public List<Channel> getAllCanali() {
    log.info("loading Channels");
    return modelMapper.modelMapper()
        .map(canaliRepository.findAllFetching(), new TypeToken<List<Channel>>() {
        }.getType());
  }

  public List<PspChannelPaymentType> getPaymentServiceProvidersChannels() {
    log.info("loading PspChannels");
    return pspCanaleTipoVersamentoRepository.findAllFetching().stream().map(p -> {
      return new PspChannelPaymentType(p.getPsp().getIdPsp(), p.getCanale().getIdCanale(),
          p.getTipoVersamento().getTipoVersamento());
    }).collect(Collectors.toList());
  }

  public List<PaymentServiceProvider> getAllPaymentServiceProviders() {
    log.info("loading Psps");
    return modelMapper.modelMapper()
        .map(pspRepository.findAll(), new TypeToken<List<PaymentServiceProvider>>() {
        }.getType());
  }

  public List<CreditorInstitutionEncoding> getCreditorInstitutionEncodings() {
    log.info("loading PaEncodings");
    return modelMapper.modelMapper().map(codifichePaRepository.findAllFetching(),
        new TypeToken<List<CreditorInstitutionEncoding>>() {
        }.getType());
  }

  public List<Encoding> getEncodings() {
    log.info("loading Encodings");
    return modelMapper.modelMapper()
        .map(codificheRepository.findAll(), new TypeToken<List<Encoding>>() {
        }.getType());
  }

  private Base64.Encoder encoder = Base64.getEncoder().withoutPadding();

  private String toXml(TplInformativaPSP element) {
    try {
      JAXBElement<TplInformativaPSP> informativaPSP = new it.gov.pagopa.apiconfig.template.ObjectFactory().createInformativaPSP(
          element);
      JAXBContext jc = JAXBContext.newInstance(element.getClass());
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
          "http://www.w3.org/2001/XMLSchema-instance");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.marshal(informativaPSP, baos);
      return encoder.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return e.toString();
    }
  }

  private String toXml(CtListaInformativePSP element) {
    try {
      JAXBElement<CtListaInformativePSP> informativaPSP = new it.gov.pagopa.apiconfig.catalogodati.ObjectFactory().createListaInformativePSP(
          element);
      JAXBContext jc = JAXBContext.newInstance(element.getClass());
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
          "http://www.w3.org/2001/XMLSchema-instance");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.marshal(informativaPSP, baos);
      return encoder.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return e.toString();
    }
  }

  private String toXml(String name, CtListaInformativeControparte element) {
    try {
      JAXBElement<CtListaInformativeControparte> informativaPA = new it.gov.pagopa.apiconfig.controparti.ObjectFactory().createListaInformativeControparte(
          element);
      JAXBContext jc = JAXBContext.newInstance(element.getClass());
      Marshaller marshaller = jc.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
      marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
          "http://www.w3.org/2001/XMLSchema-instance");
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      marshaller.marshal(informativaPA, baos);
      return encoder.encodeToString(baos.toByteArray());
    } catch (Exception e) {
      e.printStackTrace();
      return e.toString();
    }
  }

  public List<PspInformation> getInformativePsp() {
    log.info("loading InformativePsp");
    double COSTO_CONVENZIONE_FORMAT = 100d;
    List<CdiMasterValid> masters = cdiMasterValidRepository.findAllFetching();
    List<CtListaInformativePSP> informativePspSingle = masters.stream()
        .filter(m -> !m.getCdiDetail().isEmpty()).map(cdiMaster -> {

          Psp psp = cdiMaster.getFkPsp();
          CtInformativaPSP ctInformativaPSP = new CtInformativaPSP();
          ctInformativaPSP.setCodiceABI(psp.getAbi());
          ctInformativaPSP.setCodiceBIC(psp.getBic());
          ctInformativaPSP.setIdentificativoPSP(psp.getIdPsp());
          ctInformativaPSP.setRagioneSociale(psp.getRagioneSociale());
          CtInformativaMaster ctInformativaMaster = new CtInformativaMaster();
          try {
            ctInformativaMaster.setDataInizioValidita(tsToXmlGC(cdiMaster.getDataInizioValidita()));
          } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
          }
          try {
            ctInformativaMaster.setDataPubblicazione(tsToXmlGC(cdiMaster.getDataPubblicazione()));
          } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
          }
          ctInformativaMaster.setLogoPSP("".getBytes(StandardCharsets.UTF_8));
          ctInformativaMaster.setStornoPagamento(cdiMaster.getStornoPagamento() ? 1 : 0);
          ctInformativaMaster.setUrlInformazioniPSP(cdiMaster.getUrlInformazioniPsp());
          ctInformativaMaster.setMarcaBolloDigitale(cdiMaster.getMarcaBolloDigitale() ? 1 : 0);
          ctInformativaPSP.setInformativaMaster(ctInformativaMaster);
          ctInformativaPSP.setIdentificativoFlusso(cdiMaster.getIdInformativaPsp());

          List<CtInformativaDetail> details = cdiMaster.getCdiDetail().stream().filter(
              d -> !d.getFkPspCanaleTipoVersamento().getTipoVersamento().getTipoVersamento()
                  .equals("PPAY")).map(cdiDetail -> {
            PspCanaleTipoVersamentoCanale pspCanaleTipoVersamento = cdiDetail.getFkPspCanaleTipoVersamento();

            CtIdentificazioneServizio ctIdentificazioneServizio = new CtIdentificazioneServizio();
            ctIdentificazioneServizio.setNomeServizio(cdiDetail.getNomeServizio());
            ctIdentificazioneServizio.setLogoServizio("".getBytes(StandardCharsets.UTF_8));

            CdiInformazioniServizio it = cdiDetail.getCdiInformazioniServizio().stream()
                .filter(info -> info.getCodiceLingua().equals("IT")).collect(Collectors.toList())
                .get(0);
            CtInformazioniServizio ctInformazioniServizio = new CtInformazioniServizio();
            ctInformazioniServizio.setDescrizioneServizio(it.getDescrizioneServizio());
            ctInformazioniServizio.setCodiceLingua(StCodiceLingua.fromValue(it.getCodiceLingua()));
            ctInformazioniServizio.setDisponibilitaServizio(it.getDisponibilitaServizio());
//                    ctInformazioniServizio.setLimitazioniServizio(it.getLimitazioniServizio());
            ctInformazioniServizio.setUrlInformazioniCanale(it.getUrlInformazioniCanale());
            CtListaInformazioniServizio ctListaInformazioniServizio = new CtListaInformazioniServizio();
            ctListaInformazioniServizio.getInformazioniServizio().add(ctInformazioniServizio);

            List<CtFasciaCostoServizio> fasce = cdiDetail.getCdiFasciaCostoServizio().stream()
                .map(fascia -> {
                  CtFasciaCostoServizio ctFasciaCostoServizio = new CtFasciaCostoServizio();
                  ctFasciaCostoServizio.setCostoFisso(
                      BigDecimal.valueOf(fascia.getCostoFisso()).setScale(2, RoundingMode.FLOOR));
                  ctFasciaCostoServizio.setImportoMassimoFascia(
                      BigDecimal.valueOf(fascia.getImportoMassimo())
                          .setScale(2, RoundingMode.FLOOR));
                  ctFasciaCostoServizio.setValoreCommissione(
                      (BigDecimal.valueOf(fascia.getValoreCommissione())
                          .setScale(2, RoundingMode.FLOOR)));
                  return ctFasciaCostoServizio;
                }).collect(Collectors.toList());
            CtListaFasceCostoServizio ctListaFasceCostoServizio = new CtListaFasceCostoServizio();
            ctListaFasceCostoServizio.getFasciaCostoServizio().addAll(fasce);

            List<String> buyers = cdiDetail.getCdiPreference().stream().map(p -> p.getBuyer())
                .collect(Collectors.toList());
            CtListaConvenzioni listaConvenzioni = new CtListaConvenzioni();
            listaConvenzioni.getCodiceConvenzione().addAll(buyers);

            CtInformativaDetail ctInformativaDetail = new CtInformativaDetail();
            ctInformativaDetail.setCanaleApp(cdiDetail.getCanaleApp().intValue());
            ctInformativaDetail.setIdentificativoCanale(
                pspCanaleTipoVersamento.getCanale().getIdCanale());

            List<Double> costiConvenzione = cdiDetail.getCdiPreference().stream()
                .map(p -> p.getCostoConvenzione() / COSTO_CONVENZIONE_FORMAT)
                .collect(Collectors.toList());

            CtCostiServizio costiServizio = new CtCostiServizio();
            costiServizio.setTipoCostoTransazione(1);
            costiServizio.setTipoCommissione(0);
            costiServizio.setListaFasceCostoServizio(ctListaFasceCostoServizio);
            if (!costiConvenzione.isEmpty()) {
              costiServizio.setCostoConvenzione(BigDecimal.valueOf(costiConvenzione.get(0)));
            }
            ctInformativaDetail.setCostiServizio(costiServizio);

            ctInformativaDetail.setPriorita(cdiDetail.getPriorita().intValue());
            ctInformativaDetail.setListaConvenzioni(listaConvenzioni);
            ctInformativaDetail.setIdentificativoIntermediario(
                pspCanaleTipoVersamento.getCanale().getFkIntermediarioPsp()
                    .getIdIntermediarioPsp());
            ctInformativaDetail.setIdentificazioneServizio(ctIdentificazioneServizio);
            ctInformativaDetail.setListaInformazioniServizio(ctListaInformazioniServizio);
            if (cdiDetail.getTags() != null) {
              CtListaParoleChiave ctListaParoleChiave = new CtListaParoleChiave();
              ctListaParoleChiave.getParoleChiave().addAll(
                  Arrays.stream(cdiDetail.getTags().split(";"))
                      .map(t -> StParoleChiave.fromValue(t)).collect(Collectors.toList()));
              ctInformativaDetail.setListaParoleChiave(ctListaParoleChiave);
            }
            ctInformativaDetail.setModelloPagamento(cdiDetail.getModelloPagamento().intValue());
            ctInformativaDetail.setTipoVersamento(StTipoVersamento.fromValue(
                pspCanaleTipoVersamento.getTipoVersamento().getTipoVersamento()));
            return ctInformativaDetail;
          }).collect(Collectors.toList());
          CtListaInformativaDetail listaInformativaDetail = new CtListaInformativaDetail();
          listaInformativaDetail.getInformativaDetail().addAll(details);
          ctInformativaPSP.setListaInformativaDetail(listaInformativaDetail);

          CtListaInformativePSP ctListaInformativePSP = new CtListaInformativePSP();
          ctListaInformativePSP.getInformativaPSP().add(ctInformativaPSP);
          return ctListaInformativePSP;
        }).collect(Collectors.toList());

    CtListaInformativePSP informativaPspFull = new CtListaInformativePSP();
    informativePspSingle.forEach(i -> {
      informativaPspFull.getInformativaPSP().addAll(i.getInformativaPSP());
    });

    CtListaInformativePSP informativaEmpty = new CtListaInformativePSP();

    List<PspInformation> informativePspSingleCache = informativePspSingle.stream().map(i -> {
      PspInformation informativaPSP = new PspInformation();
      informativaPSP.setPsp(i.getInformativaPSP().get(0).getIdentificativoPSP());
      informativaPSP.setInformativa(toXml(i));
      return informativaPSP;
    }).collect(Collectors.toList());

    PspInformation informativaPSPFull = new PspInformation();
    informativaPSPFull.setPsp("FULL");
    informativaPSPFull.setInformativa(toXml(informativaPspFull));

    PspInformation informativaPSPEmpty = new PspInformation();
    informativaPSPEmpty.setPsp("EMPTY");
    informativaPSPEmpty.setInformativa(toXml(informativaEmpty));

    informativePspSingleCache.add(informativaPSPFull);
    informativePspSingleCache.add(informativaPSPEmpty);
    return informativePspSingleCache;

  }

    public List<PspInformation> getTemplateInformativePsp(){
        log.info("loading TemplateInformativePsp");
        List<Psp> psps = pspRepository.findAll();
        List<PspInformation> templates = new ArrayList<>();

        psps.stream().forEach(psp->{
            try {
                Optional<CdiMasterValid> masters = cdiMasterValidRepository.findByfkPsp_objId(psp.getObjId());
                TplInformativaPSP tplInformativaPSP = new TplInformativaPSP();
                tplInformativaPSP.setRagioneSociale(DA_COMPILARE);
                tplInformativaPSP.setIdentificativoPSP(DA_COMPILARE);
                tplInformativaPSP.setCodiceABI(Objects.isNull(psp.getAbi()) ? DA_COMPILARE : psp.getAbi());
                tplInformativaPSP.setCodiceBIC(Objects.isNull(psp.getBic()) ? DA_COMPILARE : psp.getBic());
                tplInformativaPSP.setIdentificativoFlusso(DA_COMPILARE_FLUSSO);
                tplInformativaPSP.setMybankIDVS(Objects.isNull(psp.getCodiceMybank()) ? DA_COMPILARE : psp.getCodiceMybank());

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
                    tplListaInformativaDetail.getInformativaDetail().add(makeTplInformativaDetail(null, null, null, null));
                    tplInformativaPSP.setListaInformativaDetail(tplListaInformativaDetail);
                    templates.add(new PspInformation(psp.getIdPsp(), toXml(tplInformativaPSP)));
                } else {
                    tplInformativaPSP.setRagioneSociale(psp.getRagioneSociale());
                    tplInformativaPSP.setIdentificativoPSP(psp.getIdPsp());
                    TplListaInformativaDetail tplListaInformativaDetail = new TplListaInformativaDetail();
                    masters.get().getCdiDetail().stream().forEach(d -> {
                        tplListaInformativaDetail.getInformativaDetail()
                                .add(makeTplInformativaDetail(d.getFkPspCanaleTipoVersamento().getCanale().getIdCanale(),
                                        d.getFkPspCanaleTipoVersamento().getCanale().getFkIntermediarioPsp().getIdIntermediarioPsp(),
                                        d.getFkPspCanaleTipoVersamento().getTipoVersamento().getTipoVersamento(),
                                        d.getModelloPagamento()));
                    });
                    tplInformativaPSP.setListaInformativaDetail(tplListaInformativaDetail);
                    templates.add(new PspInformation(psp.getIdPsp(), toXml(tplInformativaPSP)));
                }
            } catch (Exception e){
                log.error("errore creazione template informativa psp:"+psp.getIdPsp()+" error:"+e.getMessage());
            }

        });

        return templates;

    }

    private TplInformativaDetail makeTplInformativaDetail(String idCanale,String idInter,String tv,Long modello){
        TplInformativaDetail tplInformativaDetail = new TplInformativaDetail();
        tplInformativaDetail.setCanaleApp(DA_COMPILARE);
        tplInformativaDetail.setIdentificativoCanale(Objects.isNull(idCanale)?DA_COMPILARE:idCanale);
        tplInformativaDetail.setPriorita(DA_COMPILARE);
        tplInformativaDetail.setTipoVersamento(Objects.isNull(tv)?it.gov.pagopa.apiconfig.template.StTipoVersamento.BBT:it.gov.pagopa.apiconfig.template.StTipoVersamento.fromValue(tv));
        tplInformativaDetail.setModelloPagamento(Objects.isNull(modello)?0:modello.intValue());
        tplInformativaDetail.setIdentificativoIntermediario(Objects.isNull(idInter)?DA_COMPILARE:idInter);
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
        List<TplFasciaCostoServizio> tplFasciaCostoServizios = Arrays.asList(tplFasciaCostoServizio, tplFasciaCostoServizio, tplFasciaCostoServizio);
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

        Arrays.asList(it.gov.pagopa.apiconfig.template.StCodiceLingua.IT,it.gov.pagopa.apiconfig.template.StCodiceLingua.EN,
                it.gov.pagopa.apiconfig.template.StCodiceLingua.DE, it.gov.pagopa.apiconfig.template.StCodiceLingua.FR,
                it.gov.pagopa.apiconfig.template.StCodiceLingua.SL).stream().forEach(l->{
            TplInformazioniServizio infoser = new TplInformazioniServizio();
            infoser.setCodiceLingua(it.gov.pagopa.apiconfig.template.StCodiceLingua.IT);
            infoser.setDescrizioneServizio(DA_COMPILARE);
            infoser.setDescrizioneServizio(DA_COMPILARE);
            infoser.setUrlInformazioniCanale(DA_COMPILARE);
            infoser.setLimitazioniServizio(DA_COMPILARE);
            info.getInformazioniServizio().add(infoser);
        });
        tplInformativaDetail.setListaInformazioniServizio(info);
        return tplInformativaDetail;
    }

  public List<CreditorInstitutionInformation> getInformativePa() {
    log.info("loading InformativePa");
    List<Pa> pas = paRepository.findAll();//.stream().filter(s->s.getIdDominio().equals("90000000002")).collect(Collectors.toList());

    List<Pair<String, CtListaInformativeControparte>> informativePaSingle = new ArrayList<>();
    CtListaInformativeControparte informativaPaFull = new CtListaInformativeControparte();

    pas.stream().forEach(pa -> {
      log.debug("Processing pa:" + pa.getIdDominio());
      CtListaInformativeControparte ctListaInformativeControparte = new CtListaInformativeControparte();

      CtInformativaControparte ctInformativaControparte = new CtInformativaControparte();
      ctInformativaControparte.setIdentificativoDominio(pa.getIdDominio());
      ctInformativaControparte.setRagioneSociale(pa.getRagioneSociale());
      ctInformativaControparte.setContactCenterEnteCreditore("contactCenterEnteCreditore");
      ctInformativaControparte.setPagamentiPressoPSP(pa.getPagamentoPressoPsp() ? 1 : 0);

      List<IbanValidiPerPa> ibans = pa.getIbans();
      List<CtContoAccredito> contiaccredito = ibans.stream().map(iban -> {
        String idNegozio = null;
        if (iban.getIdMerchant() != null && iban.getIdBancaSeller() != null
            && iban.getChiaveAvvio() != null && iban.getChiaveEsito() != null &&
            !iban.getIdMerchant().isEmpty() && !iban.getIdBancaSeller().isEmpty()
            && !iban.getChiaveAvvio().isEmpty() && !iban.getChiaveEsito().isEmpty()
        ) {
          idNegozio = iban.getIdMerchant();
        }
        CtContoAccredito ctContoAccredito = new CtContoAccredito();
        ctContoAccredito.setIbanAccredito(iban.getIbanAccredito());
        ctContoAccredito.setIdNegozio(idNegozio);
        ctContoAccredito.setSellerBank(iban.getIdBancaSeller());
        try {
          ctContoAccredito.setDataAttivazioneIban(tsToXmlGC(iban.getDataInizioValidita()));
        } catch (DatatypeConfigurationException e) {
          throw new RuntimeException(e);
        }
        return ctContoAccredito;
      }).collect(Collectors.toList());
      ctInformativaControparte.getInformativaContoAccredito().addAll(contiaccredito);

      List<InformativePaMaster> masters = pa.getInformativePaMasters();
      InformativePaMaster master = null;
      if (!masters.isEmpty()) {
        master = masters.get(0);
      }
      if (master != null) {
        try {
          ctInformativaControparte.setDataInizioValidita(tsToXmlGC(master.getDataInizioValidita()));
        } catch (DatatypeConfigurationException e) {
          throw new RuntimeException(e);
        }
        List<InformativePaDetail> infodetails = master.getDetails();

        List<CtErogazione> disponibilita = infodetails.stream()
            .filter(d -> d.getFlagDisponibilita())
            .map(d -> infoDetailToCtErogazione(d)).collect(Collectors.toList());
        List<CtErogazione> indisponibilita = infodetails.stream()
            .filter(d -> !d.getFlagDisponibilita())
            .map(d -> infoDetailToCtErogazione(d)).collect(Collectors.toList());
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

      informativePaSingle.add(Pair.of(pa.getIdDominio(), ctListaInformativeControparte));
      informativaPaFull.getInformativaControparte()
          .addAll(ctListaInformativeControparte.getInformativaControparte());
      log.debug("Processed  pa:" + pa.getIdDominio());
    });

    log.debug("creating cache");
    List<CreditorInstitutionInformation> informativePaSingleCache = informativePaSingle.stream()
        .map(i -> {

          CreditorInstitutionInformation informativaPA = new CreditorInstitutionInformation();
          informativaPA.setPa(i.getLeft());
          informativaPA.setInformativa(toXml(i.getLeft(), i.getRight()));
          return informativaPA;
        }).collect(Collectors.toList());

    CreditorInstitutionInformation informativaPAFull = new CreditorInstitutionInformation();
    informativaPAFull.setPa("FULL");
    informativaPAFull.setInformativa(toXml("FULL", informativaPaFull));

    informativePaSingleCache.add(informativaPAFull);
    return informativePaSingleCache;
  }

  private CtErogazione infoDetailToCtErogazione(InformativePaDetail det) {
    List<CtFasciaOraria> fasce = new ArrayList<>();
    try {
      fasce = det.getFasce().stream().map(f -> {
        CtFasciaOraria fascia = new CtFasciaOraria();
        try {
          fascia.setFasciaOrariaDa(stringToXmlGCTime(f.getOraDa()));
        } catch (DatatypeConfigurationException e) {
          throw new RuntimeException(e);
        }
        try {
          fascia.setFasciaOrariaA(stringToXmlGCTime(f.getOraA()));
        } catch (DatatypeConfigurationException e) {
          throw new RuntimeException(e);
        }
        return fascia;
      }).collect(Collectors.toList());
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
        .newXMLGregorianCalendarTime(t.getHour(), t.getMinute(), t.getSecond(),
            DatatypeConstants.FIELD_UNDEFINED);
  }

  private XMLGregorianCalendar tsToXmlGC(ZonedDateTime dateTime)
      throws DatatypeConfigurationException {
    if (dateTime == null) {
      return null;
    }
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    return DatatypeFactory.newInstance().newXMLGregorianCalendar(formatter.format(dateTime));
  }
}
