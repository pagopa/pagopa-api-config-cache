package it.gov.pagopa.apiconfig.cache.model.latest.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Builder
@Data
public class Redirect implements Serializable {

  @JsonProperty(value = "protocol")
  private Protocol protocol;

  @JsonProperty(value = "ip")
  private String ip;

  @JsonProperty(value = "path")
  private String path;

  @JsonProperty(value = "port")
  private Long port;

  @JsonProperty(value = "query_string")
  private String queryString;
}
