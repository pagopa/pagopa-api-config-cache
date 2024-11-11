package it.gov.pagopa.apiconfig.cache.model.latest.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDict implements Serializable {

  @JsonProperty(value = "key", required = true)
  private String key;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "start_date", required = true)
  private ZonedDateTime startDate;

  @JsonProperty(value = "end_date")
  private ZonedDateTime endDate;
}
