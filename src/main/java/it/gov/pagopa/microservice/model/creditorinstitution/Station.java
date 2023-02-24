package it.gov.pagopa.microservice.model.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.gov.pagopa.microservice.model.configuration.Protocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * StationDetails
 */
@EqualsAndHashCode
@Data

@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class Station {

  @JsonProperty(value = "station_code",required = true)
  private String stationCode;

  @JsonProperty(value = "enabled",required = true)
  private Boolean enabled;

  @JsonProperty(value = "version",required = true)
  private Long version;

  @JsonProperty(value = "ip",required = true)
  private String ip;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonProperty(value = "password",required = true)
  private String password;

  @JsonProperty(value = "port",required = true)
  private Long port;

  @JsonProperty(value = "protocol",required = true)
  private Protocol protocol;

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

  @JsonProperty(value = "service")
  private String service;

  @JsonProperty(value = "pof_service")
  private String pofService;

  @JsonProperty(value = "nmp_service")
  private String nmpService;

  @JsonProperty(value = "broker_code",required = true)
  private String brokerCode;

  @JsonProperty(value = "protocol_4mod")
  private Protocol protocol4Mod;

  @JsonProperty(value = "ip_4mod")
  private String ip4Mod;

  @JsonProperty(value = "port_4mod")
  private Long port4Mod;

  @JsonProperty(value = "service_4mod")
  private String service4Mod;

  @JsonProperty(value = "proxy_enabled")
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

  @JsonProperty(value = "thread_number",required = true)
  private Long threadNumber;

  @JsonProperty(value = "timeout_a",required = true)
  private Long timeoutA;

  @JsonProperty(value = "timeout_b",required = true)
  private Long timeoutB;

  @JsonProperty(value = "timeout_c",required = true)
  private Long timeoutC;

  @JsonIgnore
  private Long brokerObjId;

  @JsonProperty(value = "invio_rt_istantaneo",required = true)
  private Boolean rtInstantaneousDispatch;

  @JsonProperty(value = "target_host")
  private String targetHost;

  @JsonProperty(value = "target_port")
  private Long targetPort;

  @JsonProperty(value = "target_path")
  private String targetPath;

  @JsonProperty(value = "primitive_version",required = true)
  private Integer primitiveVersion;
}
