package it.gov.pagopa.apiconfig.model.node.v1.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class Redirect {

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
