package it.gov.pagopa.apiconfig.cache.model;


import it.gov.pagopa.apiconfig.cache.model.node.CacheSchemaVersion;
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

    private CacheSchemaVersion cacheSchemaVersion;
    private String xCacheId;
    private String xCacheTimestamp;
    private String xCacheVersion;
}
