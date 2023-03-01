package it.gov.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CDI_FASCIA_COSTO_SERVIZIO", schema = "cfg")
public class CdiFasciaCostoServizio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "IMPORTO_MINIMO", nullable = false)
    private Double importoMinimo;

    @Column(name = "IMPORTO_MASSIMO", nullable = false)
    private Double importoMassimo;

    @Column(name = "COSTO_FISSO", nullable = false)
    private Double costoFisso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_CDI_DETAIL", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CdiDetail fkCdiDetail;

    @Column(name = "VALORE_COMMISSIONE")
    private Double valoreCommissione;

    @Column(name = "CODICE_CONVENZIONE", length = 35)
    private String codiceConvenzione;

}
