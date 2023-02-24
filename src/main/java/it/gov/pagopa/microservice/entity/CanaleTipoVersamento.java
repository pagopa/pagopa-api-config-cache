package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CANALE_TIPO_VERSAMENTO", schema = "cfg")
@Builder
public class CanaleTipoVersamento implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "FK_CANALE", nullable = false, insertable = false, updatable = false)
    private Long fkCanale;

    @Column(name = "FK_TIPO_VERSAMENTO", nullable = false, insertable = false, updatable = false)
    private Long fkTipoVersamento;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_CANALE", nullable = false)
    private Canali canale;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_TIPO_VERSAMENTO", nullable = false)
    private TipiVersamento tipoVersamento;

    /*@OneToMany(fetch = FetchType.LAZY, mappedBy = "fkCanaleTipoVersamento", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PspCanaleTipoVersamento> pspCanaleTipoVersamentoList;*/

}
