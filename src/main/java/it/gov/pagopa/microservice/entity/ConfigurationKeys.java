package it.gov.pagopa.microservice.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@IdClass(ConfigurationKeysView.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "CONFIGURATION_KEYS", schema = "CFG")
@Builder(toBuilder = true)
public class ConfigurationKeys implements Serializable {

    @Id
    @Column(name = "CONFIG_CATEGORY", nullable = false, length = 255)
    private String category;

    @Column(name = "CONFIG_KEY", nullable = false, length = 255)
    private String key;

    @Column(name = "CONFIG_VALUE", nullable = false, length = 255)
    private String value;

    @Column(name = "CONFIG_DESCRIPTION", length = 255)
    private String description;

}
