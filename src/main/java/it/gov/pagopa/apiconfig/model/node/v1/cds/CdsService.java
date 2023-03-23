package it.gov.pagopa.apiconfig.model.node.v1.cds;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CdsService {

  @JsonProperty(value = "id", required = true)
  private String serviceId;

  @JsonProperty(value = "description", required = true)
  private String description;

  @JsonProperty(value = "reference_xsd", required = true)
  private String referenceXsd;

  @JsonProperty(value = "version", required = true)
  private Long version;

  @JsonProperty(value = "category", required = true)
  private String category;

  @JsonIgnore
  public String getIdentifier() {
    return serviceId;
  }
}
