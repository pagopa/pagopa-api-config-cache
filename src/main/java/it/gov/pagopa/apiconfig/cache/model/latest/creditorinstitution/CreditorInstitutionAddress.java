package it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/** CreditorInstitutionAddress */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditorInstitutionAddress implements Serializable {

  @JsonProperty(value = "location")
  private String location;

  @JsonProperty(value = "city")
  private String city;

  @JsonProperty(value = "zip_code")
  private String zipCode;

  @JsonProperty(value = "country_code")
  private String countryCode;

  @JsonProperty(value = "tax_domicile")
  private String taxDomicile;
}
