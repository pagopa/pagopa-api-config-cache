package it.gov.pagopa.apiconfig.cache.model.latest.cds;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CdsCategory implements Serializable {

  @JsonProperty(value = "description", required = true)
  private String description;
}
