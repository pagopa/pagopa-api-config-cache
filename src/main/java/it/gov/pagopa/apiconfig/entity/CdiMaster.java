package it.gov.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;


@Table(name = "CDI_MASTER", schema = "cfg")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class CdiMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_INFORMATIVA_PSP", nullable = false, length = 35)
    private String idInformativaPsp;

    @Column(name = "DATA_INIZIO_VALIDITA")
    private ZonedDateTime dataInizioValidita;

    @Column(name = "DATA_PUBBLICAZIONE")
    private ZonedDateTime dataPubblicazione;

    @Column(name = "LOGO_PSP")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] logoPsp;

    @Column(name = "URL_INFORMAZIONI_PSP")
    private String urlInformazioniPsp;

    @Column(name = "MARCA_BOLLO_DIGITALE", nullable = false)
    private Boolean marcaBolloDigitale;

    @Column(name = "STORNO_PAGAMENTO", nullable = false)
    private Boolean stornoPagamento;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_PSP", nullable = false)
    private Psp fkPsp;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_BINARY_FILE", nullable = false)
    private BinaryFile fkBinaryFile;

    @Column(name = "VERSIONE", length = 35)
    private String versione;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fkCdiMaster", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CdiDetail> cdiDetail;

}
