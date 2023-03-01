package it.gov.pagopa.apiconfig.entity;

import it.gov.pagopa.apiconfig.util.YesNoConverter;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name = "PSP", schema = "CFG")
@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Psp implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

    @Column(name = "OBJ_ID", nullable = false)
    private Long objId;

    @Column(name = "ID_PSP", nullable = false, length = 35)
    private String idPsp;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled;

    @Column(name = "ABI", length = 5)
    private String abi;

    @Column(name = "BIC", length = 11)
    private String bic;

    @Column(name = "DESCRIZIONE", length = 70)
    private String descrizione;

    @Column(name = "RAGIONE_SOCIALE", length = 70)
    private String ragioneSociale;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_INT_QUADRATURE")
    private IntermediariPsp fkIntQuadrature;

    @Column(name = "STORNO_PAGAMENTO", nullable = false)
    private Boolean stornoPagamento;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "FLAG_REPO_COMMISSIONE_CARICO_PA")
    private Boolean flagRepoCommissioneCaricoPa;

    @Column(name = "EMAIL_REPO_COMMISSIONE_CARICO_PA")
    private String emailRepoCommissioneCaricoPa;

    @Column(name = "CODICE_MYBANK", length = 35)
    private String codiceMybank;

    @Column(name = "MARCA_BOLLO_DIGITALE", nullable = false)
    private Boolean marcaBolloDigitale;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "AGID_PSP", nullable = false)
    private Boolean agidPsp;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "PSP_NODO", nullable = false)
    private Boolean pspNodo;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "PSP_AVV", nullable = false)
    private Boolean pspAvv;

    @Column(name = "CODICE_FISCALE", length = 16)
    private String codiceFiscale;

    @Column(name = "VAT_NUMBER", length = 20)
    private String vatNumber;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "psp")
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private List<PspCanaleTipoVersamentoCanale> pspCanaleTipoVersamentoList;

}
