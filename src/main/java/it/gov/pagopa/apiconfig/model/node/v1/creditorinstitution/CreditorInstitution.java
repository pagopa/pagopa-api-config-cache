package it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution;

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

  @JsonProperty(value = "creditor_institution_code", required = true)
  private String creditorInstitutionCode;

  @JsonProperty(value = "enabled", required = true)
  private Boolean enabled;

  @JsonProperty(value = "business_name")
  private String businessName;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "address")
  private CreditorInstitutionAddress address;

  @JsonProperty(value = "psp_payment", required = true)
  private Boolean pspPayment;

  @JsonProperty(value = "reporting_ftp", required = true)
  private Boolean reportingFtp;

  @JsonProperty(value = "reporting_zip", required = true)
  private Boolean reportingZip;


}
