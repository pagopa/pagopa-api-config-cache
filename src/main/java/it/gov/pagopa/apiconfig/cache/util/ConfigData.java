package it.gov.pagopa.apiconfig.cache.util;


import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigData {

    private ConfigDataV1 configDataV1;
    private String xCacheId;
    private String xCacheTimestamp;
    private String xCacheVersion;
}
