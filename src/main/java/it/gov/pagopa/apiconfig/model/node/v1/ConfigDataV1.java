package it.gov.pagopa.apiconfig.model.node.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
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
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigDataV1 {

  @JsonProperty(required = true)
  private String version;
  @JsonProperty(required = true)
  public Map<String, CreditorInstitution> creditorInstitutions;
  @JsonProperty(required = true)
  public Map<String, BrokerCreditorInstitution> creditorInstitutionBrokers;
  @JsonProperty(required = true)
  public Map<String, Station> stations;
  @JsonProperty(required = true)
  public Map<String, StationCreditorInstitution> creditorInstitutionStations;
  @JsonProperty(required = true)
  public Map<String, Encoding> encodings;
  @JsonProperty(required = true)
  public Map<String, CreditorInstitutionEncoding> creditorInstitutionEncodings;
  @JsonProperty(required = true)
  public Map<String, Iban> ibans;
  @JsonProperty(required = true)
  public Map<String, CreditorInstitutionInformation> creditorInstitutionInformations;
  @JsonProperty(required = true)
  public Map<String, PaymentServiceProvider> psps;
  @JsonProperty(required = true)
  public Map<String, BrokerPsp> pspBrokers;
  @JsonProperty(required = true)
  public Map<String, PaymentType> paymentTypes;
  @JsonProperty(required = true)
  public Map<String, PspChannelPaymentType> pspChannelPaymentTypes;
  @JsonProperty(required = true)
  public Map<String, Plugin> plugins;
  @JsonProperty(required = true)
  public Map<String, PspInformation> pspInformationTemplates;
  @JsonProperty(required = true)
  public Map<String, PspInformation> pspInformations;
  @JsonProperty(required = true)
  public Map<String, Channel> channels;
  @JsonProperty(required = true)
  public Map<String, CdsService> cdsServices;
  @JsonProperty(required = true)
  public Map<String, CdsSubject> cdsSubjects;
  @JsonProperty(required = true)
  public Map<String, CdsSubjectService> cdsSubjectServices;
  @JsonProperty(required = true)
  public Map<String, CdsCategory> cdsCategories;
  @JsonProperty(required = true)
  public Map<String, ConfigurationKey> configurations;
  @JsonProperty(required = true)
  public Map<String, FtpServer> ftpServers;
  @JsonProperty(required = true)
  public Map<String, String> languages;
  @JsonProperty(required = true)
  public Map<String, GdeConfiguration> gdeConfigurations;
  @JsonProperty(required = true)
  public Map<String, MetadataDict> metadataDict;
}
