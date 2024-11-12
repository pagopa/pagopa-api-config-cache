package it.gov.pagopa.apiconfig.cache.model.node.v1.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Service implements Serializable {

  @JsonProperty(value = "path")
  private String path;

  @JsonProperty(value = "target_host")
  private String targetHost;

  @JsonProperty(value = "target_port")
  private Long targetPort;

  @JsonProperty(value = "target_path")
  private String targetPath;
}
