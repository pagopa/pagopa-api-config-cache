package it.gov.pagopa.apiconfig.model.node.v1.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GdeConfiguration {

  @JsonProperty(value = "primitive", required = true)
  private String primitiva;

  @JsonProperty(value = "type", required = true)
  private String type;

  @JsonProperty(value = "event_hub_enabled", required = true)
  private Boolean eventHubEnabled;

  @JsonProperty(value = "event_hub_payload_enabled", required = true)
  private Boolean eventHubPayloadEnabled;

  @JsonIgnore
  public String getIdentifier() {
    return primitiva + "_" + type;
  }

}
