package it.gov.pagopa.apiconfig.cache.model.latest.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

public enum Protocol implements Serializable {
  HTTPS("HTTPS"),
  HTTP("HTTP");

  private final String value;

  Protocol(String value) {
    this.value = value;
  }

  public static Protocol fromValue(String value) {
    if (value == null) {
      return null;
    }
    Optional<Protocol> protocol =
        Arrays.stream(Protocol.values()).filter(elem -> elem.value.equals(value)).findFirst();
    if (protocol.isPresent()) {
      return protocol.get();
    } else {
      return null;
    }
  }
}
