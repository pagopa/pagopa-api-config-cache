package it.gov.pagopa.apiconfig.model.node.v1.psp;

import java.util.Arrays;
import java.util.Optional;

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
    Optional<PaymentModel> first = Arrays.stream(PaymentModel.values())
        .filter(elem -> elem.value.equals(value))
        .findFirst();
    if(first.isPresent()){
      return first.get();
    }else{
      return null;
    }
  }

  public static PaymentModel fromDatabaseValue(String databaseValue) {
    Optional<PaymentModel> first = Arrays.stream(PaymentModel.values())
        .filter(elem -> elem.databaseValue.equals(databaseValue))
        .findFirst();
    if(first.isPresent()){
      return first.get();
    }else{
      return null;
    }
  }

}
