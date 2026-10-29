package it.gov.pagopa.apiconfig.cache.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CacheKeyUtilsTest {

    private static final String DATABASE_ID = "local";
    private static final String STAKEHOLDER = "node";

    @Test
    void shouldBuildKeysWithoutCanarySuffix() {
        CacheKeyUtils utils = new CacheKeyUtils(DATABASE_ID, false);

        assertEquals("apicfg_local_node", utils.getCacheKey(STAKEHOLDER));
        assertEquals("apicfg_local_node_id", utils.getCacheIdKey(STAKEHOLDER));
        assertEquals("apicfg_local_node_in_progress", utils.getCacheKeyInProgress(STAKEHOLDER));
        assertEquals(
                "apicfg_local_node_schema_caching_in_progress",
                utils.getCacheGenerationLockKey(STAKEHOLDER));
    }

    @Test
    void shouldAppendCanarySuffixWhenCanaryEnabled() {
        CacheKeyUtils utils = new CacheKeyUtils(DATABASE_ID, true);

        assertEquals("apicfg_local_node_canary", utils.getCacheKey(STAKEHOLDER));
        assertEquals("apicfg_local_node_id_canary", utils.getCacheIdKey(STAKEHOLDER));
        assertEquals("apicfg_local_node_in_progress_canary", utils.getCacheKeyInProgress(STAKEHOLDER));
        assertEquals(
                "apicfg_local_node_schema_caching_in_progress_canary",
                utils.getCacheGenerationLockKey(STAKEHOLDER));
    }

    @Test
    void shouldUseProvidedDatabaseIdInPrefix() {
        CacheKeyUtils utils = new CacheKeyUtils("p", false);

        assertEquals("apicfg_p_node", utils.getCacheKey(STAKEHOLDER));
    }
}

