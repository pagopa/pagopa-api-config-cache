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
@Table(name = "CANALI_VIEW", schema = "cfg")
@Builder(toBuilder = true)
public class Canali implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_CANALE", nullable = false, length = 35)
    private String idCanale;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ENABLED", nullable = false)
    private Boolean enabled = false;

    @Column(name = "IP", length = 100)
    private String ip;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Column(name = "PASSWORD", length = 15)
    private String password;

    @Column(name = "PORTA", nullable = false)
    private Long porta;

    @Column(name = "PROTOCOLLO", nullable = false)
    private String protocollo;

    @Column(name = "SERVIZIO", length = 100)
    private String servizio;

    @Column(name = "DESCRIZIONE", length = 70)
    private String descrizione;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "FK_INTERMEDIARIO_PSP", nullable = false)
    private IntermediariPsp fkIntermediarioPsp;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "PROXY_ENABLED", nullable = false)
    private Boolean proxyEnabled = false;

    @Column(name = "PROXY_HOST", length = 100)
    private String proxyHost;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Column(name = "PROXY_PASSWORD", length = 15)
    private String proxyPassword;

    @Column(name = "PROXY_PORT")
    private Long proxyPort;

    @Column(name = "PROXY_USERNAME", length = 15)
    private String proxyUsername;

    @Column(name = "TARGET_HOST")
    private String targetHost;

    @Column(name = "TARGET_PORT")
    private Long targetPort;

    @Column(name = "TARGET_PATH")
    private String targetPath;

    @Column(name = "NUM_THREAD", nullable = false)
    private Long numThread;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "USE_NEW_FAULT_CODE", nullable = false)
    private Boolean useNewFaultCode = false;

    @Column(name = "TIMEOUT_A", nullable = false)
    private Long timeoutA;

    @Column(name = "TIMEOUT_B", nullable = false)
    private Long timeoutB;

    @Column(name = "TIMEOUT_C", nullable = false)
    private Long timeoutC;

    @Column(name = "SERVIZIO_NMP")
    private String servizioNmp;

    @Column(name = "REDIRECT_IP", length = 100)
    private String redirectIp;

    @Column(name = "REDIRECT_PATH", length = 100)
    private String redirectPath;

    @Column(name = "REDIRECT_PORTA")
    private Long redirectPorta;

    @Column(name = "REDIRECT_QUERY_STRING")
    private String redirectQueryString;

    @Column(name = "MODELLO_PAGAMENTO", nullable = false)
    private String modelloPagamento;

    @Column(name = "REDIRECT_PROTOCOLLO", length = 35)
    private String redirectProtocollo;

    @Column(name = "ID_SERV_PLUGIN", length = 35)
    private String idServPlugin;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "RT_PUSH", nullable = false)
    private Boolean rtPush = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "AGID_CHANNEL", nullable = false)
    private Boolean agidChannel = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ON_US", nullable = false)
    private Boolean onUs = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "RECOVERY", nullable = false)
    private Boolean recovery = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "MARCA_BOLLO_DIGITALE", nullable = false)
    private Boolean marcaBolloDigitale = false;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "FLAG_IO")
    private Boolean flagIo;

    @Column(name = "VERSIONE_PRIMITIVE")
    private Integer versionePrimitive;

}
