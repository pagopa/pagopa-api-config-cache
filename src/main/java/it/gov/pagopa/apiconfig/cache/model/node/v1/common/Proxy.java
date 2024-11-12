package it.gov.pagopa.apiconfig.cache.model.node.v1.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Proxy implements Serializable {
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
}
