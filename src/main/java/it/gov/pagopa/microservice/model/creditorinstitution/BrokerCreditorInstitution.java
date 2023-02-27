package it.gov.pagopa.microservice.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * BrokerDetails
 */
@EqualsAndHashCode
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrokerCreditorInstitution {

  @JsonProperty(value = "broker_code", required = true)
  private String brokerCode;

  @JsonProperty(value = "enabled", required = true)
  private Boolean enabled;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "extended_fault_bean", required = true)
  private Boolean extendedFaultBean;

}
