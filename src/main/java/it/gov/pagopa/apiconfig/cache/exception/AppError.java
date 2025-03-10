package it.gov.pagopa.apiconfig.cache.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AppError {
  INTERNAL_SERVER_ERROR(
      HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Something was wrong"),
  CACHE_SCHEMA_NOT_VALID(
          HttpStatus.INTERNAL_SERVER_ERROR, "Cache schema error", "Cache schema not valid"),
  CACHE_ID_NOT_FOUND(HttpStatus.NOT_FOUND, "Cache id not found", "No Cache id found with key %s"),
  CACHE_NOT_INITIALIZED(HttpStatus.NOT_FOUND, "Cache not initialized", "Call refresh first"),
  CACHE_NOT_READABLE(HttpStatus.INTERNAL_SERVER_ERROR, "Cache not readable", "Call refresh first");

  public final HttpStatus httpStatus;
  public final String title;
  public final String details;

  AppError(HttpStatus httpStatus, String title, String details) {
    this.httpStatus = httpStatus;
    this.title = title;
    this.details = details;
  }
}
