package it.gov.pagopa.apiconfig.model.node.v1.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** PaymentType */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PaymentType {

  @JsonProperty(value = "payment_type", required = true)
  private String paymentTypeCode;

  @JsonProperty(value = "description")
  private String description;
}
