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
 * CreditorInstitution
 */
@EqualsAndHashCode
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class CreditorInstitution {

  @JsonProperty(value = "creditor_institution_code")
  private String creditorInstitutionCode;

  @JsonProperty(value = "enabled")
  private Boolean enabled;

  @JsonProperty(value = "business_name")
  private String businessName;

  @JsonProperty(value = "address")
  private CreditorInstitutionAddress address;

  @JsonProperty(value = "psp_payment")
  private Boolean pspPayment;

  @JsonProperty(value = "reporting_ftp")
  private Boolean reportingFtp;

  @JsonProperty(value = "reporting_zip")
  private Boolean reportingZip;


}
