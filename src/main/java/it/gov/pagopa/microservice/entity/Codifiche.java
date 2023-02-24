package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "CODIFICHE", schema = "cfg")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Codifiche {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_CODIFICA", nullable = false, length = 35)
    private String idCodifica;

    @Column(name = "DESCRIZIONE", length = 35)
    private String descrizione;

}
