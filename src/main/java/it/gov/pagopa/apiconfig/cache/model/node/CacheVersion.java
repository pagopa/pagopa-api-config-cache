package it.gov.pagopa.apiconfig.cache.model.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CacheVersion {

  @JsonProperty(required = true)
  private String version;
}
