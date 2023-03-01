package it.gov.pagopa.apiconfig.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class ConfigurationKeysView implements Serializable {

    @Id
    @Column(name = "CONFIG_CATEGORY", nullable = false)
    private String category;

    @Id
    @Column(name = "CONFIG_KEY", nullable = false)
    private String key;

}
