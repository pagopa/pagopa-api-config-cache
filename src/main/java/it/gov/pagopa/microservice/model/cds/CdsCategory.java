package it.gov.pagopa.microservice.model.cds;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class CdsCategory {

  @JsonProperty(value = "description",required = true)
  private String description;

}
