package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "DIZIONARIO_METADATI", schema = "cfg")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DizionarioMetadati {
  @Id
  @Column(name = "CHIAVE", nullable = false)
  private String key;

  @Column(name = "DESCRIZIONE", nullable = false)
   private String descr;
  @Column(name = "DATA_INIZIO_VALIDITA")
  private ZonedDateTime dataInizioValidita;

  @Column(name = "DATA_FINE_VALIDITA")
  private ZonedDateTime dataFineValidita;

}
