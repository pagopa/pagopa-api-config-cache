package it.gov.pagopa.microservice.model.cds;

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

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CdsSubjectService {

  @JsonProperty(value = "subject",required = true)
  private String subject;

  @JsonProperty(value = "service",required = true)
  private String service;

  @JsonProperty(value = "subject_service_id",required = true)
  private String subjectServiceId;

  @JsonProperty(value = "start_date")
  private ZonedDateTime startDate;

  @JsonProperty(value = "end_date")
  private ZonedDateTime endDate;

  @JsonProperty(value = "fee",required = true)
  private Boolean fee;

}
