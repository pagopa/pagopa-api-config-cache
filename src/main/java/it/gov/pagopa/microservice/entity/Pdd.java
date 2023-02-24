package it.gov.pagopa.microservice.entity;

import it.gov.pagopa.microservice.util.YesNoConverter;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "PDD", schema = "cfg")
@Builder(toBuilder = true)
public class Pdd implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_PDD", nullable = false, length = 35)
    private String idPdd;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "descrizione", nullable = false, length = 35)
    private String descrizione;

    @Column(name = "IP", nullable = false, length = 15)
    private String ip;

    @Column(name = "porta")
    private Integer porta;

}
