package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "CODIFICHE_PA", schema = "cfg")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class CodifichePa {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "CODICE_PA", nullable = false, length = 35)
    private String codicePa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_CODIFICA", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Codifiche fkCodifica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_PA", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pa fkPa;

}
