package it.gov.pagopa.microservice.model.psp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.microservice.model.configuration.Protocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class Channel {

  @JsonProperty(value = "channel_code",required = true)
  private String channelCode;

  @JsonProperty(value = "enabled",required = true)
  private Boolean enabled;

  @JsonProperty(value = "password",required = true)
  private String password;

  @JsonProperty(value = "protocol",required = true)
  private Protocol protocol;

  @JsonProperty(value = "ip",required = true)
  private String ip;

  @JsonProperty(value = "port",required = true)
  private Long port;

  @JsonProperty(value = "service",required = true)
  private String service;

  @JsonProperty(value = "broker_psp_code",required = true)
  private String brokerPspCode;

  @JsonProperty(value = "proxy_enabled",required = true)
  private Boolean proxyEnabled;

  @JsonProperty(value = "proxy_host")
  private String proxyHost;

  @JsonProperty(value = "proxy_port")
  private Long proxyPort;

  @JsonProperty(value = "proxy_username")
  private String proxyUsername;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonProperty(value = "proxy_password")
  private String proxyPassword;

  @JsonProperty(value = "target_host")
  private String targetHost;

  @JsonProperty(value = "target_port")
  private Long targetPort;

  @JsonProperty(value = "target_path")
  private String targetPath;

  @JsonProperty(value = "thread_number",required = true)
  private Long threadNumber;

  @JsonProperty(value = "timeout_a",required = true)
  private Long timeoutA;

  @JsonProperty(value = "timeout_b",required = true)
  private Long timeoutB;

  @JsonProperty(value = "timeout_c",required = true)
  private Long timeoutC;

  @JsonProperty(value = "npm_service",required = true)
  private String npmService;

  @JsonProperty(value = "new_fault_code",required = true)
  private Boolean newFaultCode;

  @JsonProperty(value = "redirect_ip")
  private String redirectIp;

  @JsonProperty(value = "redirect_path")
  private String redirectPath;

  @JsonProperty(value = "redirect_port")
  private Long redirectPort;

  @JsonProperty(value = "redirect_query_string")
  private String redirectQueryString;

  @JsonProperty(value = "redirect_protocol")
  private Protocol redirectProtocol;

  @JsonProperty(value = "payment_model",required = true)
  private PaymentModel paymentModel;

  @JsonProperty(value = "serv_plugin")
  private String servPlugin;

  @JsonProperty(value = "rt_push",required = true)
  private Boolean rtPush;

  @JsonProperty(value = "recovery",required = true)
  private Boolean recovery;

  @JsonProperty(value = "digital_stamp_brand",required = true)
  private Boolean digitalStampBrand;

  @JsonProperty(value = "flag_io",required = true)
  private Boolean flagIo;

  @JsonProperty(value = "agid",required = true)
  private Boolean agid;

  @JsonProperty(value = "primitive_version",required = true)
  private Integer primitiveVersion;

}
