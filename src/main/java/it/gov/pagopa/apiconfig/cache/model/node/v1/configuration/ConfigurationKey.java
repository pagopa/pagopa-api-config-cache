package it.gov.pagopa.apiconfig.cache.model.node.v1.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/** ConfigurationKey */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ConfigurationKey implements Serializable {

  @JsonProperty(value = "category", required = true)
  private String category;

  @JsonProperty(value = "key", required = true)
  private String key;

  @JsonProperty(value = "value", required = true)
  private String value;

  @JsonProperty(value = "description")
  private String description;

  @JsonIgnore
  public String getIdentifier() {
    return category + "-" + key;
  }
}
