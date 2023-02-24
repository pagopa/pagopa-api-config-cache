package it.gov.pagopa.microservice.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;



/**
 * Stations
 */
@EqualsAndHashCode
@Data

@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class StationCreditorInstitution {

  @JsonProperty(value = "creditor_institution_code")
  private String creditorInstitutionCode;

  @JsonProperty(value = "station_code")
  private String stationCode;
  @JsonProperty(value = "application_code")
  private Long applicationCode;

  @JsonProperty(value = "aux_digit")
  private Long auxDigit;

  @JsonProperty(value = "segregation_code")
  private Long segregationCode;

  @JsonProperty(value = "mod4")
  private Boolean mod4;

  @JsonProperty(value = "broadcast")
  private Boolean broadcast;

  @JsonIgnore
  public String getIdentifier() {
    return stationCode + "_" + creditorInstitutionCode + "_" + auxDigit + "_" + applicationCode
        + "_" + segregationCode;
  }

}
