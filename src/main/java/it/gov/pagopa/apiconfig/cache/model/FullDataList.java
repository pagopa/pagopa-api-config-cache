package it.gov.pagopa.apiconfig.cache.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
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
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.*;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FullDataList implements Serializable {

    @JsonProperty(required = true)
    private String version;

    @JsonProperty(required = true)
    private ZonedDateTime timestamp;

    @JsonProperty(required = true)
    private String cacheVersion;

    @JsonProperty(required = true)
    private List<CreditorInstitution> creditorInstitutions;

    @GraphQLQuery(name = "creditorInstitutions")
    public List<CreditorInstitution> findCreditorInstitution(
            @GraphQLArgument(name = "creditorInstitutionCode") String creditorInstitutionCode,
            @GraphQLArgument(name = "enabled") Boolean enabled) {

        if (creditorInstitutionCode != null) {
            return this.creditorInstitutions.stream()
                    .filter(ci -> ci.getCreditorInstitutionCode().equals(creditorInstitutionCode))
                    .collect(Collectors.toList());
        }
        return this.creditorInstitutions;
    }

    @GraphQLQuery(name = "creditorInstitutions")
    public List<CreditorInstitution> getEnabled(
            @GraphQLArgument(name = "enabled") Boolean enabled) {

        if (enabled != null) {
            return this.creditorInstitutions.stream()
                    .filter(ci -> ci.getEnabled().equals(enabled))
                    .collect(Collectors.toList());
        }
        return this.creditorInstitutions;
    }

}
