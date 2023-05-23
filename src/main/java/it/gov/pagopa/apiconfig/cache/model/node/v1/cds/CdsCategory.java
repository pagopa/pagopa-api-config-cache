package it.gov.pagopa.apiconfig.cache.model.node.v1.cds;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CdsCategory implements Serializable {

  @JsonProperty(value = "description", required = true)
  private String description;
}
