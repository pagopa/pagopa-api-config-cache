package it.gov.pagopa.apiconfig.entity;

import it.gov.pagopa.apiconfig.util.YesNoConverter;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "FTP_SERVERS", schema = "cfg")
@Builder(toBuilder = true)
public class FtpServers implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "HOST", nullable = false)
    private String host;

    @Column(name = "PORT", nullable = false)
    private Integer port;

    @Column(name = "USERNAME", nullable = false, length = 35)
    private String username;

    @Column(name = "PASSWORD", nullable = false, length = 35)
    private String password;

    @Column(name = "ROOT_PATH", nullable = false)
    private String rootPath;

    @Column(name = "SERVICE", nullable = false)
    private String service;

    @Column(name = "TYPE", nullable = false)
    private String type;

    @Column(name = "IN_PATH")
    private String inPath;

    @Column(name = "OUT_PATH")
    private String outPath;

    @Column(name = "HISTORY_PATH")
    private String historyPath;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "ENABLED")
    private Boolean enabled;

}
