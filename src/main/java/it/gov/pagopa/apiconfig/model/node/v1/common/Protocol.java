package it.gov.pagopa.apiconfig.model.node.v1.common;

import java.util.Arrays;
import java.util.Optional;

public enum Protocol {
  HTTPS("HTTPS"),
  HTTP("HTTP");

  private final String value;

  Protocol(String value) {
    this.value = value;
  }

  public static Protocol fromValue(String value) {
    if(value == null){
      return null;
    }
    Optional<Protocol> protocol = Arrays.stream(Protocol.values())
        .filter(elem -> elem.value.equals(value))
        .findFirst();
    if(protocol.isPresent()){
      return protocol.get();
    } else {
      return null;
    }
  }
}