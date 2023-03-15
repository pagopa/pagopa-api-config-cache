package it.gov.pagopa.apiconfig.model.node.v1.psp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data

@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PspChannelPaymentType {

  @JsonProperty(value = "psp_code", required = true)
  private String pspCode;
  @JsonProperty(value = "channel_code", required = true)
  private String channelCode;
  @JsonProperty(value = "payment_type", required = true)
  private String paymentType;

  @JsonIgnore
  public String getIdentifier() {
    return pspCode + "_" + channelCode + "_" + paymentType;
  }

}
