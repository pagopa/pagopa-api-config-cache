package it.gov.pagopa.apiconfig.cache.model.node.v1.cds;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CdsSubject implements Serializable {

  @JsonProperty(value = "creditor_institution_code", required = true)
  private String creditorInstitutionCode;

  @JsonProperty(value = "creditor_institution_description", required = true)
  private String creditorInstitutionDescription;
}
