package it.gov.pagopa.apiconfig.cache.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class RedisRepositoryTest {

  private static final long DEFAULT_TTL_MINUTES = 1440L;

  @Mock private RedisTemplate<String, byte[]> redisTemplateObj;

  @Mock private ValueOperations<String, byte[]> valueOps;

  @InjectMocks private RedisRepository redisRepository;

  private final byte[] value = "value".getBytes(StandardCharsets.UTF_8);
  private final byte[] keyValue = "keyValue".getBytes(StandardCharsets.UTF_8);

  @Test
  void save_setsValueWithTtl() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);

    redisRepository.save("k", value, 10);

    verify(valueOps).set("k", value, Duration.ofMinutes(10));
  }

  @Test
  void get_returnsValueFromRedis() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("k")).thenReturn(value);

    assertThat(redisRepository.get("k")).isEqualTo(value);
  }

  @Test
  void pushToRedisSync_storesBothKeysWithDefaultTtl() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);

    redisRepository.pushToRedisSync("k", "kid", value, keyValue);

    verify(valueOps).set("k", value, Duration.ofMinutes(DEFAULT_TTL_MINUTES));
    verify(valueOps).set("kid", keyValue, Duration.ofMinutes(DEFAULT_TTL_MINUTES));
  }

  @Test
  void pushToRedisAsyncSingleKey_storesOnlyThatKey() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);

    redisRepository.pushToRedisAsync("k", value);

    verify(valueOps).set("k", value, Duration.ofMinutes(DEFAULT_TTL_MINUTES));
    verify(valueOps, never()).set(eq("kid"), any(), any());
  }

  @Test
  void pushToRedisSync_ko() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    doThrow(new RuntimeException("redis down")).when(valueOps).set(eq("k"), any(), any());

    assertThrows(
        RuntimeException.class, () -> redisRepository.pushToRedisSync("k", "kid", value, keyValue));
  }

  @Test
  void getStringByKeyId_returnsDecodedString_whenPresent() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("s")).thenReturn("hello".getBytes(StandardCharsets.UTF_8));

    assertThat(redisRepository.getStringByKeyId("s")).isEqualTo("hello");
  }

  @Test
  void getStringByKeyId_returnsNull_whenAbsent() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("missing")).thenReturn(null);

    assertThat(redisRepository.getStringByKeyId("missing")).isNull();
  }

  @Test
  void getStringByKeyId_returnsNull_whenRedisThrows() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("boom")).thenThrow(new RuntimeException("redis down"));

    assertThat(redisRepository.getStringByKeyId("boom")).isNull();
  }

  @Test
  void getBooleanByKeyId_returnsTrue_whenValueIsOne() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("flag")).thenReturn("1".getBytes(StandardCharsets.UTF_8));

    assertThat(redisRepository.getBooleanByKeyId("flag")).isTrue();
  }

  @Test
  void getBooleanByKeyId_returnsFalse_whenValueIsNotOne() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("flag")).thenReturn("0".getBytes(StandardCharsets.UTF_8));

    assertThat(redisRepository.getBooleanByKeyId("flag")).isFalse();
  }

  @Test
  void getBooleanByKeyId_returnsFalse_whenAbsent() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("missing")).thenReturn(null);

    assertThat(redisRepository.getBooleanByKeyId("missing")).isFalse();
  }

  @Test
  void getBooleanByKeyId_returnsFalse_whenRedisThrows() {
    when(redisTemplateObj.opsForValue()).thenReturn(valueOps);
    when(valueOps.get("boom")).thenThrow(new RuntimeException("redis down"));

    assertThat(redisRepository.getBooleanByKeyId("boom")).isFalse();
  }
}
