package it.gov.pagopa.apiconfig.cache.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import it.gov.pagopa.apiconfig.cache.model.latest.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.Channel;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.PspChannelPaymentType;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.PspInformation;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullData implements Serializable {

    @JsonProperty(required = true)
    private String version;

    @JsonProperty(required = true)
    private ZonedDateTime timestamp;

    @JsonProperty(required = true)
    private String cacheVersion;

    @JsonProperty(required = true)
    private Map<String, CreditorInstitution> creditorInstitutions;

    @JsonProperty(required = true)
    private Map<String, BrokerCreditorInstitution> creditorInstitutionBrokers;

    @JsonProperty(required = true)
    private Map<String, Station> stations;

    @JsonProperty(required = true)
    private Map<String, StationCreditorInstitution> creditorInstitutionStations;

    @JsonProperty(required = true)
    private Map<String, MaintenanceStation> maintenanceStations;

    @JsonProperty(required = true)
    private Map<String, Encoding> encodings;

    @JsonProperty(required = true)
    private Map<String, CreditorInstitutionEncoding> creditorInstitutionEncodings;

    @JsonProperty(required = true)
    private Map<String, Iban> ibans;

    @JsonProperty(required = true)
    private Map<String, CreditorInstitutionInformation> creditorInstitutionInformations;

    @JsonProperty(required = true)
    private Map<String, PaymentServiceProvider> psps;

    @JsonProperty(required = true)
    private Map<String, BrokerPsp> pspBrokers;

    @JsonProperty(required = true)
    private Map<String, PaymentType> paymentTypes;

    @JsonProperty(required = true)
    private Map<String, PspChannelPaymentType> pspChannelPaymentTypes;

    @JsonProperty(required = true)
    private Map<String, Plugin> plugins;

    @JsonProperty(required = true)
    private Map<String, PspInformation> pspInformationTemplates;

    @JsonProperty(required = true)
    private Map<String, PspInformation> pspInformations;

    @JsonProperty(required = true)
    private Map<String, Channel> channels;

    @JsonProperty(required = true)
    private Map<String, CdsService> cdsServices;

    @JsonProperty(required = true)
    private Map<String, CdsSubject> cdsSubjects;

    @JsonProperty(required = true)
    private Map<String, CdsSubjectService> cdsSubjectServices;

    @JsonProperty(required = true)
    private Map<String, CdsCategory> cdsCategories;

    @JsonProperty(required = true)
    private Map<String, ConfigurationKey> configurations;

    @JsonProperty(required = true)
    private Map<String, FtpServer> ftpServers;

    @JsonProperty(required = true)
    private Map<String, String> languages;

    @JsonProperty(required = true)
    private Map<String, GdeConfiguration> gdeConfigurations;

    @JsonProperty(required = true)
    private Map<String, MetadataDict> metadataDict;
}
