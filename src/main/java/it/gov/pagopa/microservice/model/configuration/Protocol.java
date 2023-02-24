package it.gov.pagopa.microservice.model.configuration;

import java.util.Arrays;

public enum Protocol {
  HTTPS("HTTPS"),
  HTTP("HTTP");

  private final String value;

  Protocol(String value) {
    this.value = value;
  }

  public static Protocol fromValue(String value) {
    return Arrays.stream(Protocol.values())
        .filter(elem -> elem.value.equals(value))
        .findFirst().get();
  }
}
