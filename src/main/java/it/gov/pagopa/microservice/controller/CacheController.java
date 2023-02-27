package it.gov.pagopa.microservice.controller;

import it.gov.pagopa.microservice.model.ConfigData;
import it.gov.pagopa.microservice.model.cds.CdsService;
import it.gov.pagopa.microservice.model.cds.CdsCategory;
import it.gov.pagopa.microservice.model.cds.CdsSubject;
import it.gov.pagopa.microservice.model.cds.CdsSubjectService;
import it.gov.pagopa.microservice.model.configuration.ConfigurationKey;
import it.gov.pagopa.microservice.model.configuration.FtpServer;
import it.gov.pagopa.microservice.model.configuration.GdeConfiguration;
import it.gov.pagopa.microservice.model.configuration.MetadataDict;
import it.gov.pagopa.microservice.model.configuration.PaymentType;
import it.gov.pagopa.microservice.model.configuration.Plugin;
import it.gov.pagopa.microservice.model.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.microservice.model.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.microservice.model.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.microservice.model.creditorinstitution.Encoding;
import it.gov.pagopa.microservice.model.creditorinstitution.Iban;
import it.gov.pagopa.microservice.model.creditorinstitution.StationCreditorInstitution;
import it.gov.pagopa.microservice.model.creditorinstitution.Station;
import it.gov.pagopa.microservice.model.psp.BrokerPsp;
import it.gov.pagopa.microservice.model.psp.Channel;
import it.gov.pagopa.microservice.model.psp.PaymentServiceProvider;
import it.gov.pagopa.microservice.model.psp.PspChannelPaymentType;
import it.gov.pagopa.microservice.service.ConfigService;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Slf4j
public class CacheController {

  @Value("${server.servlet.context-path}")
  String basePath;

  @Autowired
  private ConfigService configService;

  private ConfigData cache = null;

  @GetMapping("/cache")
  public ConfigData cache() throws IOException {
    if (cache == null) {
      return newCache();
    }else{
      return cache;
    }
  }


  private ConfigData newCache() throws IOException {

    long startTime = System.nanoTime();

    ConfigData configData = new ConfigData();
    List<BrokerCreditorInstitution> intpa = configService.getBrokerDetails();
    Map intpamap = new HashMap<String, BrokerCreditorInstitution>();
    intpa.stream().forEach(k->{
      intpamap.put(k.getBrokerCode(),k);
    });
    configData.setIntermediariPa(intpamap);

    List<BrokerPsp> intpsp = configService.getBrokerPspDetails();
    Map intpspmap = new HashMap<String, BrokerPsp>();
    intpsp.stream().forEach(k->{
      intpspmap.put(k.getBrokerPspCode(),k);
    });
    configData.setIntermediariPsp(intpspmap);

    List<CdsCategory> cdscats = configService.getCdsCategories();
    Map cdscatsMap = new HashMap<String, CdsCategory>();
    cdscats.stream().forEach(k->cdscatsMap.put(k.getDescription(),k));
    configData.setCdsCategorie(cdscatsMap);

    List<CdsService> cdsServices = configService.getCdsServices();
    Map cdsServicesMap = new HashMap<String, CdsService>();
    cdsServices.stream().forEach(k->cdsServicesMap.put(k.getIdentifier(),k));
    configData.setCdsServizi(cdsServicesMap);

    List<CdsSubject> cdsSubjects = configService.getCdsSubjects();
    Map cdsSubjectsMap = new HashMap<String, CdsSubject>();
    cdsSubjects.stream().forEach(k->cdsSubjectsMap.put(k.getCreditorInstitutionCode(),k));
    configData.setCdsSoggetti(cdsSubjectsMap);

    List<CdsSubjectService> cdsSubjectServices = configService.getCdsSubjectServices();
    Map cdsSubjectServicesMap = new HashMap<String,CdsSubjectService>();
    cdsSubjectServices.stream().forEach(k->cdsSubjectServicesMap.put(k.getSubjectServiceId(),k));
    configData.setCdsSoggettiServizi(cdsSubjectServicesMap);

    List<GdeConfiguration> gde = configService.getGdeConfiguration();
    Map gdeMap = new HashMap<String, GdeConfiguration>();
    gde.stream().forEach(k->{
      gdeMap.put(k.getIdentifier(),k);
    });
    configData.setConfigurazioniGde(gdeMap);

    List<MetadataDict> meta = configService.getMetadataDict();
    Map metaMap = new HashMap<String,MetadataDict>();
    meta.stream().forEach(k->{
      metaMap.put(k.getKey(),k);
    });
    configData.setDizionarioMetadati(metaMap);

    List<ConfigurationKey> configurationKeyList = configService.getConfigurationKeys();
    Map configMap = new HashMap<String,ConfigurationKey>();
    configurationKeyList.stream().forEach(k->{
      configMap.put(k.getIdentifier(),k);
    });
    configData.setConfigurazioni(configMap);

    List<FtpServer> ftpservers = configService.getFtpServers();
    Map ftpserversMap = new HashMap<String,FtpServer>();
    ftpservers.stream().forEach(k->{
      ftpserversMap.put(k.getIdentifier(),k);
    });
    configData.setFtpServers(ftpserversMap);

    Map codiciLingua = new HashMap<String,String>();
    codiciLingua.put("IT","IT");
    codiciLingua.put("DE","DE");
    configData.setCodiciLingua(codiciLingua);

    List<Plugin> plugins = configService.getWfespPluginConfigurations();
    Map pluginsMap = new HashMap<String,Plugin>();
    plugins.stream().forEach(k->{
      pluginsMap.put(k.getIdServPlugin(),k);
    });
    configData.setPlugins(pluginsMap);

    List<PaymentServiceProvider> psps = configService.getAllPaymentServiceProviders();
    Map pspMap = new HashMap<String, PaymentServiceProvider>();
    psps.stream().forEach(k->{
      pspMap.put(k.getPspCode(),k);
    });
    configData.setPsps(pspMap);

    List<Channel> canali = configService.getAllCanali();
    Map canalimap = new HashMap<String, Channel>();
    canali.stream().forEach(k->{
      canalimap.put(k.getChannelCode(),k);
    });
    configData.setCanali(canalimap);
    List<PaymentType> tipiv = configService.getPaymentTypes();
    Map tipivMap = new HashMap<String, PaymentType>();
    tipiv.stream().forEach(k->{
      tipivMap.put(k.getPaymentTypeCode(),k);
    });

    configData.setTipiVersamento(tipivMap);
    List<PspChannelPaymentType> pspChannels = configService.getPaymentServiceProvidersChannels();
    Map pspChannelsMap = new HashMap<String, PspChannelPaymentType>();
    pspChannels.stream().forEach(k->{
      pspChannelsMap.put(k.getIdentifier(),k);
    });
    configData.setPspCanaliTipiVersamento(pspChannelsMap);

    List<CreditorInstitution> pas = configService.getCreditorInstitutions();
    Map pamap = new HashMap<String, CreditorInstitution>();
    pas.stream().forEach(k->{
      pamap.put(k.getCreditorInstitutionCode(),k);
    });
    configData.setPas(pamap);

    List<Encoding> encodings = configService.getEncodings();
    Map encodingsMap = new HashMap<String,Encoding>();
    encodings.stream().forEach(k->{
      encodingsMap.put(k.getCodeType(),k);
    });
    configData.setCodifiche(encodingsMap);

    List<CreditorInstitutionEncoding> ciencodings = configService.getCreditorInstitutionEncodings();
    Map ciencodingsMap = new HashMap<String, CreditorInstitutionEncoding>();
    ciencodings.stream().forEach(k->{
      ciencodingsMap.put(k.getIdentifier(),k);
    });
    configData.setCodifichePa(ciencodingsMap);

    List<StationCreditorInstitution> paspa = configService.findAllPaStazioniPa();
    Map paspamap = new HashMap<String, StationCreditorInstitution>();
    paspa.stream().forEach(k->{
      paspamap.put(k.getIdentifier(),k);
    });
    configData.setStazioniPa(paspamap);

    List<Station> stazioni = configService.findAllStazioni();
    Map stazionimap = new HashMap<String, Station>();
    stazioni.stream().forEach(k->{
      stazionimap.put(k.getStationCode(),k);
    });
    configData.setStazioni(stazionimap);
    List<Iban> ibans = configService.getCurrentIbans();
    Map ibansMap = new HashMap<String,Encoding>();
    ibans.stream().forEach(k->{
      ibansMap.put(k.getIdentifier(),k);
    });
    configData.setIbans(ibansMap);
/*
    List<InformativaPSP> infopsps = configService.getInformativePsp();
    Map infopspsMap = new HashMap<String, InformativaPSP>();
    infopsps.stream().forEach(k->{
      infopspsMap.put(k.getPsp(),k);
    });

    configData.setInformativeCdi(infopspsMap);

    List<InformativaPA> infopas = configService.getInformativePa();
    Map infopasMap = new HashMap<String,InformativaPA>();
    infopas.stream().forEach(k->{
      infopasMap.put(k.getPa(),k);
    });
    configData.setInformativePa(infopasMap);
*/
    cache = configData;
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;
    log.info("cache loaded in "+duration+"ms");

//    String stringData = new ConfigParser().writeConfig(configData);
//        pushToRedis(stringData.getBytes(StandardCharsets.UTF_8));
    return configData;
  }

}
