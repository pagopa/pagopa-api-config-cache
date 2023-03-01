package it.gov.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString
@Entity
@Table(name = "CDI_PREFERENCES", schema = "cfg")
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CdiPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_INFORMATIVA_DETAIL", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CdiDetail cdiDetail;

    @Column(name = "SELLER", nullable = false)
    private String seller;

    @Column(name = "BUYER")
    private String buyer;

    @Column(name = "COSTO_CONVENZIONE", nullable = false)
    private Double costoConvenzione;

}
