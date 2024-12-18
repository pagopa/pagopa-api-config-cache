package it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/** Encoding */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Encoding implements Serializable {

  @JsonProperty(value = "code_type", required = true)
  private String codeType;

  @JsonProperty(value = "description", required = true)
  private String description;
}
