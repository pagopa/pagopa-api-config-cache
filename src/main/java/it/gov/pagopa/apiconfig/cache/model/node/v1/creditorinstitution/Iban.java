package it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/** Iban */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Iban {

  @JsonProperty(value = "iban", required = true)
  private String ibanValue;

  @JsonProperty(value = "creditor_institution_code", required = true)
  private String creditorInstitutionCode;

  @JsonProperty(value = "validity_date", required = true)
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime validityDate;

  @JsonProperty(value = "publication_date", required = true)
  @JsonSerialize(using = ZonedDateTimeSerializer.class)
  private ZonedDateTime publicationDate;

  @JsonProperty(value = "shop_id")
  private String idNegozio;

  @JsonProperty(value = "seller_bank_id")
  private String idSellerBank;

  @JsonProperty(value = "avvio_key")
  private String chiaveAvvio;

  @JsonProperty(value = "esito_key")
  private String chiaveEsito;

  @JsonIgnore
  public String getIdentifier() {
    return creditorInstitutionCode + "-" + ibanValue;
  }
}
