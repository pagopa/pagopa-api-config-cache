package it.gov.pagopa.apiconfig.cache.model.latest.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class Timeouts implements Serializable {
  @JsonProperty(value = "timeout_a", required = true)
  private Long timeoutA;

  @JsonProperty(value = "timeout_b", required = true)
  private Long timeoutB;

  @JsonProperty(value = "timeout_c", required = true)
  private Long timeoutC;
}
