package it.gov.pagopa.microservice.entity;

import it.gov.pagopa.microservice.util.YesNoConverter;
import lombok.*;

import javax.persistence.*;

@Table(name = "PA_STAZIONE_PA", schema = "cfg")
@Entity
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaStazionePa {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "PROGRESSIVO")
    private Long progressivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_PA", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pa pa;

    @Column(name = "FK_PA", nullable = false, insertable = false, updatable = false)
    private Long fkPa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_STAZIONE", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Stazioni stazione;

    @Column(name = "AUX_DIGIT")
    private Long auxDigit;

    @Column(name = "SEGREGAZIONE")
    private Long segregazione;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "QUARTO_MODELLO", nullable = false)
    private Boolean quartoModello = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "STAZIONE_AVV", nullable = false)
    private Boolean stazioneAvv = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "STAZIONE_NODO", nullable = false)
    private Boolean stazioneNodo = true;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "BROADCAST", nullable = false)
    private Boolean broadcast = false;

}
