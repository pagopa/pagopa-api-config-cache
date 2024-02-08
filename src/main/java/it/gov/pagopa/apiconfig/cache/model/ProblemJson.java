package it.gov.pagopa.apiconfig.cache.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Object returned as response in case of an error.
 *
 * <p>See {@link it.pagopa.microservice.exception.ErrorHandler}
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemJson {

  @JsonProperty(value = "title")
  @Schema(
      description =
          "A short, summary of the problem type. Written in english and readable for engineers"
              + " (usually not suited for non technical stakeholders and not localized); example:"
              + " Service Unavailable")
  private String title;

  @JsonProperty(value = "status")
  @Schema(
      example = "200",
      description =
          "The HTTP status code generated by the origin server for this occurrence of the problem.")
  @Min(100)
  @Max(600)
  private Integer status;

  @JsonProperty(value = "detail")
  @Schema(
      example = "There was an error processing the request",
      description = "A human readable explanation specific to this occurrence of the problem.")
  private String detail;
}
