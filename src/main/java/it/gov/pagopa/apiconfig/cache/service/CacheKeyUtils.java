package it.gov.pagopa.apiconfig.cache.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CacheKeyUtils {

  private static final String CANARY_SUFFIX = "_canary";

  /** Prefix built once from config, e.g. {@code apicfg_<databaseId>}. */
  private final String keyPrefix;

  /** Suffix appended to every key when the instance runs as canary. */
  private final String keySuffix;

  public CacheKeyUtils(
      @Value("${spring.database.id}") String databaseId, @Value("${canary:false}") boolean canary) {
    this.keyPrefix = "apicfg_" + databaseId + "_";
    this.keySuffix = canary ? CANARY_SUFFIX : "";
  }

  public String getCacheKeyInProgress(String stakeholder) {
    return buildKey(stakeholder, "_in_progress");
  }

  public String getCacheKey(String stakeholder) {
    return buildKey(stakeholder, "");
  }

  public String getCacheIdKey(String stakeholder) {
    return buildKey(stakeholder, "_id");
  }

  public String getCacheGenerationLockKey(String stakeholder) {
    return buildKey(stakeholder, "_schema_caching_in_progress");
  }

  private String buildKey(String stakeholder, String subKey) {
    return keyPrefix + stakeholder + subKey + keySuffix;
  }
}
