package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "INFORMATIVE_PA_FASCE", schema = "cfg")
public class InformativePaFasce {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ORA_A", length = 35)
    private String oraA;

    @Column(name = "ORA_DA", length = 35)
    private String oraDa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_INFORMATIVA_PA_DETAIL", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private InformativePaDetail fkInformativaPaDetail;

}
