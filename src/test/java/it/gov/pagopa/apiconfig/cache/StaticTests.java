package it.gov.pagopa.apiconfig.cache;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import it.gov.pagopa.apiconfig.cache.model.NodeCacheKey;
import it.gov.pagopa.apiconfig.cache.model.StringToNodeCacheKeyConverter;
import org.junit.jupiter.api.Test;

class StaticTests {

  @Test
  void checkKeyConverter() {
    StringToNodeCacheKeyConverter stringToNodeCacheKeyConverter =
        new StringToNodeCacheKeyConverter();
    assertThat(stringToNodeCacheKeyConverter.convert("psps").equals(NodeCacheKey.PSPS));
  }
}
