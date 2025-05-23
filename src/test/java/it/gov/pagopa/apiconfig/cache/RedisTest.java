package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class RedisTest {

  @InjectMocks private RedisRepository redisRepository;
  @Mock() private RedisTemplate<String, Map<String,Object>> redisTemplate;
  @Mock() private RedisTemplate<String, Object> redisTemplateObj;
  @Spy() private TestValueOperation testValueOperation = new TestValueOperation();

  @BeforeEach
  void setUp() {
    org.springframework.test.util.ReflectionTestUtils.setField(
        redisRepository, "redisTemplateObj", redisTemplateObj);
  }

  @Test
  void test() {
    Map<String, Object> configDataV1 = new HashMap<>();
    configDataV1.put(Constants.VERSION,"test");
    when(redisTemplateObj.opsForValue()).thenReturn(testValueOperation);

    redisRepository.pushToRedisAsync("k", "kid", "configDataV1".getBytes(StandardCharsets.UTF_8),"test".getBytes(StandardCharsets.UTF_8));
    verify(testValueOperation, times(2)).set(any(), any(), any());
    assertThat(testValueOperation.get("k").equals(redisRepository.get("k")));

    testValueOperation.set("b", "1".getBytes(StandardCharsets.UTF_8));
    redisRepository.pushToRedisAsync("b", (byte[])testValueOperation.get("b"));
    assertThat(testValueOperation.get("b").equals(redisRepository.getBooleanByKeyId("b")));

    testValueOperation.set("s", "test1".getBytes(StandardCharsets.UTF_8));
    redisRepository.pushToRedisAsync("s", (byte[])testValueOperation.get("s"));
    assertThat(testValueOperation.get("s").equals(redisRepository.getStringByKeyId("s")));

    redisRepository.remove("s");
    verify(redisTemplateObj, times(1)).delete("s");

    assertThat(redisRepository.getStringByKeyId("no") == null);
    assertThat(!redisRepository.getBooleanByKeyId("no"));
  }

  @Test
  void testzip() throws IOException {
    assertThat(ZipUtils.unzip(ZipUtils.zip("AAA".getBytes(StandardCharsets.UTF_8)))).isEqualTo("AAA".getBytes(StandardCharsets.UTF_8));
  }
}
