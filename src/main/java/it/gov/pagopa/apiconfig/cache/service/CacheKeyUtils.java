package it.gov.pagopa.apiconfig.cache.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheKeyUtils {

    @Value("#{'${canary}'=='true' ? '_canary' : ''}")
    private String KEY_SUFFIX;

    @Value("apicfg_${spring.database.id}_{{stakeholder}}_in_progress")
    private String CACHE_KEY_IN_PROGRESS;


    @Value("apicfg_${spring.database.id}_{{stakeholder}}")
    private String CACHE_KEY;

    @Value("apicfg_${spring.database.id}_{{stakeholder}}_id")
    private String CACHE_ID_KEY;

    private final String STAKEHOLDER_PLACEHOLDER = "{{stakeholder}}";

    @Value("apicfg_${spring.database.id}_{{stakeholder}}_schema_caching_in_progress")
    private String CACHE_GENERATION_LOCK_KEY;

    public String getCacheKeyInProgress(String stakeholder) {
        return CACHE_KEY_IN_PROGRESS.replace(STAKEHOLDER_PLACEHOLDER, stakeholder) + KEY_SUFFIX;
    }

    public String getCacheKey(String stakeholder) {
        return CACHE_KEY.replace(STAKEHOLDER_PLACEHOLDER, stakeholder) + KEY_SUFFIX;
    }

    public String getCacheIdKey(String stakeholder) {
        return CACHE_ID_KEY.replace(STAKEHOLDER_PLACEHOLDER, stakeholder) + KEY_SUFFIX;
    }

    public String getCacheGenerationLockKey(String stakeholder) {
        return CACHE_GENERATION_LOCK_KEY.replace(STAKEHOLDER_PLACEHOLDER, stakeholder) + KEY_SUFFIX;
    }
}
