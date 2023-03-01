package it.gov.pagopa.apiconfig.entity;

import lombok.*;

import javax.persistence.*;

@Table(name = "BINARY_FILE", schema = "cfg")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class BinaryFile {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "OBJ_ID", nullable = false)
    private Long id;

    @Column(name = "FILE_CONTENT", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] fileContent;

    @Column(name = "FILE_HASH", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private byte[] fileHash;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Column(name = "SIGNATURE_TYPE", length = 30)
    private String signatureType;

    @Lob
    @Column(name = "XML_FILE_CONTENT")
    private String xmlFileContent;

}
