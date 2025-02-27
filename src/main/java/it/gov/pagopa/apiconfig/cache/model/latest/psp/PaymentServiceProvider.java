package it.gov.pagopa.apiconfig.cache.model.latest.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class PaymentServiceProvider implements Serializable {

  @JsonProperty(value = "psp_code", required = true)
  private String pspCode;

  @JsonProperty(value = "enabled", required = true)
  private Boolean enabled;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "business_name")
  private String businessName;

  @JsonProperty(value = "abi")
  private String abi;

  @JsonProperty(value = "bic")
  private String bic;

  @JsonProperty(value = "my_bank_code")
  private String myBankCode;

  @JsonProperty(value = "digital_stamp", required = true)
  private Boolean digitalStamp;

  @JsonProperty(value = "agid_psp", required = true)
  private Boolean agidPsp;

  @JsonProperty(value = "tax_code")
  private String taxCode;

  @JsonProperty(value = "vat_number")
  private String vatNumber;
}
