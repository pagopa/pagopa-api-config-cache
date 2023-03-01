package it.gov.pagopa.apiconfig.model.node.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.apiconfig.model.node.v1.cds.CdsService;
import it.gov.pagopa.apiconfig.model.node.v1.cds.CdsCategory;
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
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigDataV1 {

  @JsonProperty(required = true)
  public Map<String, CreditorInstitution> pas;
  @JsonProperty(required = true)
  public Map<String, BrokerCreditorInstitution> intermediariPa;
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
