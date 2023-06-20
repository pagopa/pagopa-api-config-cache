package it.gov.pagopa.apiconfig.cache;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class RedisTest {

  @InjectMocks private RedisRepository redisRepository;
  @Mock() private RedisTemplate<String, ConfigDataV1> redisTemplate;
  @Mock() private RedisTemplate<String, Object> redisTemplateObj;
  @Spy() private TestValueOperation testValueOperation = new TestValueOperation();

  @BeforeEach
  void setUp() {
    org.springframework.test.util.ReflectionTestUtils.setField(
        redisRepository, "redisTemplate", redisTemplate);
    org.springframework.test.util.ReflectionTestUtils.setField(
        redisRepository, "redisTemplate", redisTemplateObj);
  }

  @Test
  void test() {
    ConfigDataV1 configDataV1 = new ConfigDataV1();
    configDataV1.setVersion("test");
    when(redisTemplateObj.opsForValue()).thenReturn(testValueOperation);

    redisRepository.pushToRedisAsync("k", "kid", configDataV1);
    verify(testValueOperation, times(2)).set(any(), any(), any());
    assertThat(testValueOperation.get("k").equals(redisRepository.getConfigDataV1("k")));

    testValueOperation.set("b", Boolean.TRUE);
    redisRepository.pushToRedisAsync("b", testValueOperation.get("b"));
    assertThat(testValueOperation.get("b").equals(redisRepository.getBooleanByKeyId("b")));

    testValueOperation.set("s", "test1");
    redisRepository.pushToRedisAsync("s", testValueOperation.get("s"));
    assertThat(testValueOperation.get("s").equals(redisRepository.getStringByKeyId("s")));

    redisRepository.remove("s");
    verify(redisTemplateObj, times(1)).delete("s");

    assertThat(redisRepository.getStringByKeyId("no") == null);
    assertThat(!redisRepository.getBooleanByKeyId("no"));
  }
}
