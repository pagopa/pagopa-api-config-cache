package it.gov.pagopa.apiconfig.cache.model.node.v1.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Connection implements Serializable {

  @JsonProperty(value = "protocol", required = true)
  private Protocol protocol;

  @JsonProperty(value = "ip", required = true)
  private String ip;

  @JsonProperty(value = "port", required = true)
  private Long port;
}
