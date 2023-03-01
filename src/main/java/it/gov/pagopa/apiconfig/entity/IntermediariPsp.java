package it.gov.pagopa.apiconfig.entity;

import it.gov.pagopa.apiconfig.util.YesNoConverter;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "INTERMEDIARI_PSP", schema = "cfg")
@Entity
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class IntermediariPsp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_INTERMEDIARIO_PSP", nullable = false, length = 35)
    private String idIntermediarioPsp;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "CODICE_INTERMEDIARIO")
    private String codiceIntermediario;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "INTERMEDIARIO_AVV", nullable = false)
    private Boolean intermediarioAvv;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "INTERMEDIARIO_NODO", nullable = false)
    private Boolean intermediarioNodo;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "FAULT_BEAN_ESTESO", nullable = false)
    private Boolean faultBeanEsteso;

}
