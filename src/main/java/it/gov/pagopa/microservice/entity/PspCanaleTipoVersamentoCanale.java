package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "PSP_CANALE_TIPO_VERSAMENTO_CANALE", schema = "cfg")
@Setter
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PspCanaleTipoVersamentoCanale implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "FK_CANALE", nullable = false, insertable = false, updatable = false)
    private Long fkCanale;

    @Column(name = "FK_PSP", nullable = false, insertable = false, updatable = false)
    private Long fkPsp;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_TIPO_VERSAMENTO", nullable = false)
    @EqualsAndHashCode.Exclude
    private TipiVersamento tipoVersamento;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_PSP", nullable = false)
    @EqualsAndHashCode.Exclude
    private Psp psp;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_CANALE", nullable = false)
    @EqualsAndHashCode.Exclude
    private Canali canale;


}
