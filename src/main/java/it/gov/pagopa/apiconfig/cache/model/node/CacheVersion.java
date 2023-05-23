package it.gov.pagopa.apiconfig.cache.model.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CacheVersion implements Serializable {

  @JsonProperty(required = true)
  private String version;
}
