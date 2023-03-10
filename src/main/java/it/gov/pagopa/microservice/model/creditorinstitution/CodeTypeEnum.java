package it.gov.pagopa.microservice.model.creditorinstitution;

import java.util.Arrays;
import lombok.Getter;

public enum CodeTypeEnum {
  QR_CODE("QR-CODE", false),
  BARCODE_128_AIM("BARCODE-128-AIM", false),
  BARCODE_GS1_128("BARCODE-GS1-128", true);

  @Getter
  private final String value;
  @Getter
  private final boolean deprecated;

  CodeTypeEnum(String value, boolean deprecated) {
    this.value = value;
    this.deprecated = deprecated;
  }

  public static CodeTypeEnum fromValue(String value) {
    return Arrays.stream(CodeTypeEnum.values())
        .filter(elem -> elem.value.equals(value))
        .findFirst().get();
  }
}
