package it.gov.pagopa.microservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.microservice.model.cds.CdsService;
import it.gov.pagopa.microservice.model.cds.CdsCategory;
import it.gov.pagopa.microservice.model.cds.CdsSubject;
import it.gov.pagopa.microservice.model.cds.CdsSubjectService;
import it.gov.pagopa.microservice.model.configuration.*;
import it.gov.pagopa.microservice.model.creditorinstitution.*;
import it.gov.pagopa.microservice.model.psp.*;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigData {

  @JsonProperty(required = true)
  public Map<String, CreditorInstitution> pas;
  @JsonProperty(required = true)
  public Map<String, BrokerDetails> intermediariPa;
  @JsonProperty(required = true)
  public Map<String, Station> stazioni;
  @JsonProperty(required = true)
  public Map<String, StationCreditorInstitution> stazioniPa;
  @JsonProperty(required = true)
  public Map<String, Encoding> codifiche;
  @JsonProperty(required = true)
  public Map<String, CreditorInstitutionEncoding> codifichePa;
  @JsonProperty(required = true)
  public Map<String, Iban> ibans;
  @JsonProperty(required = true)
  public Map<String, CreditorInstitutionInformation> informativePa;
  @JsonProperty(required = true)
  public Map<String, PaymentServiceProvider> psps;
  @JsonProperty(required = true)
  public Map<String, BrokerPsp> intermediariPsp;
  @JsonProperty(required = true)
  public Map<String, PaymentType> tipiVersamento;
  @JsonProperty(required = true)
  public Map<String, PspChannelPaymentType> pspCanaliTipiVersamento;
  @JsonProperty(required = true)
  public Map<String, Plugin> plugins;
  @JsonProperty(required = true)
  public Map<String, PspInformation> informativeCdiTemplate;
  @JsonProperty(required = true)
  public Map<String, PspInformation> informativeCdi;
  @JsonProperty(required = true)
  public Map<String, Channel> canali;
  @JsonProperty(required = true)
  public Map<String, CdsService> cdsServizi;
  @JsonProperty(required = true)
  public Map<String, CdsSubject> cdsSoggetti;
  @JsonProperty(required = true)
  public Map<String, CdsSubjectService> cdsSoggettiServizi;
  @JsonProperty(required = true)
  public Map<String, CdsCategory> cdsCategorie;
  @JsonProperty(required = true)
  public Map<String, ConfigurationKey> configurazioni;
  @JsonProperty(required = true)
  public Map<String, FtpServer> ftpServers;
  @JsonProperty(required = true)
  public Map<String, String> codiciLingua;
  @JsonProperty(required = true)
  public Map<String, GdeConfiguration> configurazioniGde;
  @JsonProperty(required = true)
  public Map<String, MetadataDict> dizionarioMetadati;
}
