package it.gov.pagopa.microservice.entity;

import it.gov.pagopa.microservice.util.YesNoConverter;
import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "CDS_SOGGETTO_SERVIZIO", schema = "cfg")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CdsSoggettoServizio {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
  @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
  @Column(name = "OBJ_ID", nullable = false)
  private Long id;

  @Column(name = "FK_CDS_SOGGETTO", nullable = false, insertable = false, updatable = false)
   private String fkCdsSoggetto;
  @Column(name = "FK_CDS_SERVIZIO", nullable = false, insertable = false, updatable = false)
   private String fkCdsServizio;
  @Column(name = "ID_SOGGETTO_SERVIZIO")
  private String idSoggettoServizio;
  @Column(name = "DATA_INIZIO_VALIDITA")
  private ZonedDateTime dataInizioValidita;

  @Column(name = "DATA_FINE_VALIDITA")
  private ZonedDateTime dataFineValidita;

  @Column(name = "COMMISSIONE")
  @Convert(converter = YesNoConverter.class)
  private Boolean commissione;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "FK_CDS_SOGGETTO", nullable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private CdsSoggetto soggetto;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "FK_CDS_SERVIZIO", nullable = false)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private CdsServizio servizio;

}
