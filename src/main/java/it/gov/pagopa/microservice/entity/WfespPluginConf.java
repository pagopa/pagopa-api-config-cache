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
@Table(name = "WFESP_PLUGIN_CONF", schema = "cfg")
@Builder(toBuilder = true)
public class WfespPluginConf implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)

    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "ID_SERV_PLUGIN", nullable = false, length = 35)
    private String idServPlugin;

    @Column(name = "PROFILO_PAG_CONST_STRING", length = 150)
    private String profiloPagConstString;

    @Column(name = "PROFILO_PAG_SOAP_RULE", length = 150)
    private String profiloPagSoapRule;

    @Column(name = "PROFILO_PAG_RPT_XPATH", length = 150)
    private String profiloPagRptXpath;

    @Column(name = "ID_BEAN")
    private String idBean;

}
