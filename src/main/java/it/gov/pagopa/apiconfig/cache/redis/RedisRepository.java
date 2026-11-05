package it.gov.pagopa.apiconfig.cache.redis;

import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisRepository {

  private static final long CACHE_TTL_MINUTES = 1440;
  private static final Duration CACHE_TTL = Duration.ofMinutes(CACHE_TTL_MINUTES);

  private final RedisTemplate<String, byte[]> redisTemplateObj;

  public RedisRepository(RedisTemplate<String, byte[]> redisObjectTemplate) {
    this.redisTemplateObj = redisObjectTemplate;
  }

  public void save(String key, byte[] value, long ttl) {
    redisTemplateObj.opsForValue().set(key, value, Duration.ofMinutes(ttl));
  }

  public byte[] get(String key) {
    return redisTemplateObj.opsForValue().get(key);
  }

  /**
   * Save the cache and its id on Redis atomically: both keys are written in a single {@code
   * MULTI/EXEC} transaction, so Redis never holds a new payload with an old id (or vice versa).
   *
   * @throws AppException with {@link AppError#CACHE_SAVE_ERROR} if the transaction fails
   */
  public void pushToRedisSync(String key, String keyId, byte[] object, byte[] keyObject) {
    List<Object> results;
    try {
      log.debug("Sync saving keys {} and {} on redis", key, keyId);
      results = redisTemplateObj.execute(transaction(key, keyId, object, keyObject));
    } catch (Exception e) {
      throw new AppException(AppError.CACHE_SAVE_ERROR, e, key);
    }
    if (results == null || results.stream().anyMatch(Throwable.class::isInstance)) {
      throw new AppException(
          AppError.CACHE_SAVE_ERROR, new IllegalStateException("Transaction not applied"), key);
    }
    log.info("Sync saved {} on redis, id {}", key, new String(keyObject, StandardCharsets.UTF_8));
  }

  @SuppressWarnings("unchecked")
  private SessionCallback<List<Object>> transaction(
      String key, String keyId, byte[] object, byte[] keyObject) {
    return new SessionCallback<>() {
      @Override
      public <K, V> List<Object> execute(@NonNull RedisOperations<K, V> operations) {
        RedisOperations<String, byte[]> ops = (RedisOperations<String, byte[]>) operations;
        ops.multi();
        try {
          ops.opsForValue().set(key, object, CACHE_TTL);
          ops.opsForValue().set(keyId, keyObject, CACHE_TTL);
          return ops.exec();
        } catch (RuntimeException e) {
          ops.discard();
          throw e;
        }
      }
    };
  }

  @Async
  public void pushToRedisAsync(String key, byte[] object) {
    try {
      log.debug("Async saving {} on redis", key);
      save(key, object, CACHE_TTL_MINUTES);
      log.info("Async saved {} on redis", key);
    } catch (Exception e) {
      log.error("Could not async-save key {} on redis", key, e);
    }
  }

  String getStringByKeyId(String keyId) {
    byte[] v = null;
    try {
      v = get(keyId);
    } catch (Exception e) {
      log.error("Could not get key {} from redis", keyId, e);
    }
    if (v != null) {
      return new String(v, StandardCharsets.UTF_8);
    } else {
      return null;
    }
  }

  Boolean getBooleanByKeyId(String keyId) {
    byte[] v = null;
    try {
      v = get(keyId);
    } catch (Exception e) {
      log.error("Could not get key {} from redis", keyId, e);
    }
    if (v != null) {
      return "1".equals(new String(v, StandardCharsets.UTF_8));
    } else {
      return Boolean.FALSE;
    }
  }
}
