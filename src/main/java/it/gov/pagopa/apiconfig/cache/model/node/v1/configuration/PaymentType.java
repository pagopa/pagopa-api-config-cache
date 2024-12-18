package it.gov.pagopa.apiconfig.cache.model.node.v1.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/** PaymentType */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class PaymentType implements Serializable {

  @JsonProperty(value = "payment_type", required = true)
  private String paymentTypeCode;

  @JsonProperty(value = "description")
  private String description;
}
