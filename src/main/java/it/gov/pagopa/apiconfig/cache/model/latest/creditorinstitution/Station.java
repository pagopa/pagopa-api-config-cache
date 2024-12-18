package it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

/** StationDetails */
@EqualsAndHashCode
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class Station implements Serializable {

  @JsonProperty(value = "station_code", required = true)
  private String stationCode;

  @JsonProperty(value = "enabled", required = true)
  private Boolean enabled;

  @JsonProperty(value = "version", required = true)
  private Long version;

  @JsonProperty(value = "connection", required = true)
  private Connection connection;

  @JsonProperty(value = "connection_mod4")
  private Connection mod4connection;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @JsonProperty(value = "password", required = true)
  private String password;

  @JsonProperty(value = "redirect", required = true)
  private Redirect redirect;

  @JsonProperty(value = "service")
  private Service service;

  @JsonProperty(value = "service_pof")
  private Service pofService;

  @JsonProperty(value = "service_mod4")
  private Service mod4Service;

  @JsonProperty(value = "broker_code", required = true)
  private String brokerCode;

  @JsonProperty(value = "proxy")
  private Proxy proxy;

  @JsonProperty(value = "thread_number", required = true)
  private Long threadNumber;

  @JsonProperty(value = "timeouts", required = true)
  private Timeouts timeouts;

  @JsonIgnore private Long brokerObjId;

  @JsonProperty(value = "invio_rt_istantaneo", required = true)
  private Boolean rtInstantaneousDispatch;

  @JsonProperty(value = "primitive_version", required = true)
  private Integer primitiveVersion;

  @JsonProperty(value = "flag_standin")
  private Boolean flagStandIn;

  @JsonProperty("is_payment_options_enabled")
  private Boolean isPaymentOptionsEnabled = false;

  @JsonProperty("rest_endpoint")
  private String restEndpoint;
}
