package it.gov.pagopa.apiconfig.cache.model.latest.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.apiconfig.cache.model.latest.common.Connection;
import it.gov.pagopa.apiconfig.cache.model.latest.common.Proxy;
import it.gov.pagopa.apiconfig.cache.model.latest.common.Redirect;
import it.gov.pagopa.apiconfig.cache.model.latest.common.Service;
import it.gov.pagopa.apiconfig.cache.model.latest.common.Timeouts;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class Channel implements Serializable {

  @JsonProperty(value = "channel_code", required = true)
  private String channelCode;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "enabled", required = true)
  private Boolean enabled;

  @JsonProperty(value = "password", required = true)
  private String password;

  @JsonProperty(value = "connection", required = true)
  private Connection connection;

  @JsonProperty(value = "broker_psp_code", required = true)
  private String brokerPspCode;

  @JsonProperty(value = "proxy")
  private Proxy proxy;

  @JsonProperty(value = "service")
  private Service service;

  @JsonProperty(value = "service_nmp")
  private Service nmpService;

  @JsonProperty(value = "thread_number", required = true)
  private Long threadNumber;

  @JsonProperty(value = "timeouts", required = true)
  private Timeouts timeouts;

  @JsonProperty(value = "new_fault_code", required = true)
  private Boolean newFaultCode;

  @JsonProperty(value = "redirect", required = true)
  private Redirect redirect;

  @JsonProperty(value = "payment_model", required = true)
  private String paymentModel;

  @JsonProperty(value = "serv_plugin")
  private String servPlugin;

  @JsonProperty(value = "rt_push", required = true)
  private Boolean rtPush;

  @JsonProperty(value = "recovery", required = true)
  private Boolean recovery;

  @JsonProperty(value = "digital_stamp", required = true)
  private Boolean digitalStamp;

  @JsonProperty(value = "flag_io", required = true)
  private Boolean flagIo;

  @JsonProperty(value = "agid", required = true)
  private Boolean agid;

  @JsonProperty(value = "primitive_version", required = true)
  private Integer primitiveVersion;

  @JsonProperty(value = "flag_travaso", required = true)
  private Boolean flagTravaso;

  @JsonProperty(value = "flag_standin")
  private Boolean flagStandIn;
}
