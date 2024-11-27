package it.gov.pagopa.apiconfig.cache.model.node.v1.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/** BrokerDetails */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class BrokerPsp implements Serializable {

  @JsonProperty(value = "broker_psp_code", required = true)
  private String brokerPspCode;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "enabled", required = true)
  private Boolean enabled;

  @JsonProperty(value = "extended_fault_bean", required = true)
  private Boolean extendedFaultBean;
}
