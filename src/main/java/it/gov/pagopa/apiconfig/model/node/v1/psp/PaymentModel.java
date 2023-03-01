package it.gov.pagopa.apiconfig.model.node.v1.psp;

import java.util.Arrays;

public enum PaymentModel {

  IMMEDIATE("IMMEDIATE", "IMMEDIATO"),
  IMMEDIATE_MULTIBENEFICIARY("IMMEDIATE_MULTIBENEFICIARY", "IMMEDIATO_MULTIBENEFICIARIO"),
  DEFERRED("DEFERRED", "DIFFERITO"),
  ACTIVATED_AT_PSP("ACTIVATED_AT_PSP", "ATTIVATO_PRESSO_PSP");

  private final String value;
  private final String databaseValue;

  PaymentModel(String value, String databaseValue) {
    this.value = value;
    this.databaseValue = databaseValue;
  }

  public static PaymentModel fromValue(String value) {
    return Arrays.stream(PaymentModel.values())
        .filter(elem -> elem.value.equals(value))
        .findFirst().get();
  }

  public static PaymentModel fromDatabaseValue(String databaseValue) {
    return Arrays.stream(PaymentModel.values())
        .filter(elem -> elem.databaseValue.equals(databaseValue))
        .findFirst().get();
  }

}
