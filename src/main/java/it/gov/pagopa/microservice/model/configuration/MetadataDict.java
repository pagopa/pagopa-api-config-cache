package it.gov.pagopa.microservice.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDict {

  @JsonProperty(value = "key", required = true)
  private String key;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "start_date",required = true)
  private ZonedDateTime startDate;

  @JsonProperty(value = "end_date")
  private ZonedDateTime endDate;

}
