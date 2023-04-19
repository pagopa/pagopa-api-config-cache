package it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Encoding */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionEncoding {

  @JsonProperty(value = "code_type", required = true)
  private String codeType;

  @JsonProperty(value = "encoding_code", required = true)
  private String encodingCode;

  @JsonProperty(value = "creditor_institution_code", required = true)
  private String creditorInstitutionCode;

  @JsonIgnore private Long paObjId;

  @JsonIgnore private Long codificheObjId;

  @JsonIgnore
  public String getIdentifier() {
    return encodingCode;
  }
}
