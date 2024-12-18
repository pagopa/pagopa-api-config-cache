package it.gov.pagopa.apiconfig.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.model.NodeCacheKey;
import it.gov.pagopa.apiconfig.cache.model.StringToNodeCacheKeyConverter;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.ObjectRedisSerializer;
import it.gov.pagopa.apiconfig.cache.util.JsonSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class StaticTests {

  @Test
  void checkSerializer() {
    ObjectRedisSerializer<ConfigDataV1> serializer = new ObjectRedisSerializer<>();
    ConfigDataV1 cd = new ConfigDataV1();
    cd.setVersion("version1");
    byte[] serialized = serializer.serialize(cd);
    assertThat(serializer.deserialize(serialized).equals(cd));

    assertThat(serializer.deserialize(null) == null);
  }

  @Test
  void checkKeyConverter() {
    StringToNodeCacheKeyConverter stringToNodeCacheKeyConverter =
        new StringToNodeCacheKeyConverter();
    assertThat(stringToNodeCacheKeyConverter.convert("psps").equals(NodeCacheKey.PSPS));
  }

  @Test
  void jsonSerializer() throws IOException {
    JsonSerializer serializer = new JsonSerializer();
    ObjectMapper objectMapper = new ObjectMapper();
    org.springframework.test.util.ReflectionTestUtils.setField(serializer, "objectMapper", objectMapper);
    Map test = new HashMap<>();
    assertThat(serializer.serialize(test)).isNotNull();
  }


}
