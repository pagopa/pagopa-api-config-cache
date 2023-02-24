package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Table(name = "INFORMATIVE_PA_MASTER", schema = "cfg")
@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class InformativePaMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_INFORMATIVA_PA", nullable = false, length = 35)
    private String idInformativaPa;

    @Column(name = "DATA_INIZIO_VALIDITA")
    private ZonedDateTime dataInizioValidita;

    @Column(name = "DATA_PUBBLICAZIONE")
    private ZonedDateTime dataPubblicazione;

    @ManyToOne(optional = false)
    @JoinColumn(name = "FK_PA", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pa fkPa;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "FK_BINARY_FILE")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BinaryFile fkBinaryFile;

    @Column(name = "VERSIONE", length = 35)
    private String versione;

    @Column(name = "PAGAMENTI_PRESSO_PSP")
    private Boolean pagamentiPressoPsp;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fkInformativaPaMaster", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<InformativePaDetail> details;
}
