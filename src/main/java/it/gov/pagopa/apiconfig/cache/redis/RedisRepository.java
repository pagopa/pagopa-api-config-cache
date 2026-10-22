package it.gov.pagopa.apiconfig.cache.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Component
@Slf4j
public class RedisRepository {

  private final RedisTemplate<String, byte[]> redisTemplateObj;

  public RedisRepository(@Qualifier("object") RedisTemplate<String, byte[]> redisTemplateObj) {
    this.redisTemplateObj = redisTemplateObj;
  }

  public void save(String key, byte[] value, long ttl) {
    redisTemplateObj.opsForValue().set(key, value, Duration.ofMinutes(ttl));
  }

  /**
   * Returns true if the value does not exists and is stored, false if already present.
   *
   * @param key
   * @param value
   * @param ttl
   * @return
   */
  public Boolean saveIfAbsent(String key, byte[] value, long ttl) {
      return redisTemplateObj.opsForValue().setIfAbsent(key, value, Duration.ofMinutes(ttl));
  }

  public byte[] get(String key) {
    return redisTemplateObj.opsForValue().get(key);
  }

  public void remove(String key) {
    redisTemplateObj.delete(key);
  }

  @Async
  public void pushToRedisAsync(String key, String keyId, byte[] object, byte[] keyobject) {
    try {
      log.info("async saving {} on redis", key);
      save(key, object, 1440);
      save(keyId, keyobject, 1440);
      log.info("async saved {} on redis, id {}", key, new String(keyobject, StandardCharsets.UTF_8));
    } catch (Exception e) {
      log.error("could not async-save on redis", e);
    }
  }

  public void pushToRedisSync(String key, String keyId, byte[] object, byte[] keyobject) {
    try {
      log.info("sync saving {} on redis", key);
      save(key, object, 1440);
      save(keyId, keyobject, 1440);
      log.info("sync saved {} on redis, id {}", key, new String(keyobject, StandardCharsets.UTF_8));
    } catch (Exception e) {
      log.error("could not sync save on redis", e);
    }
  }

  @Async
  public void pushToRedisAsync(String key, byte[] object) {
    try {
      log.info("async saving {} on redis", key);
      save(key, object, 1440);
      log.info("async saved {} on redis", key);
    } catch (Exception e) {
      log.error("could not async-save " + key + "on redis", e);
    }
  }

  public String getStringByKeyId(String keyId) {
    byte[] v = null;
    try {
      v = get(keyId);
    } catch (Exception e) {
      log.error("could not get key " + keyId + " from redis", e);
    }
    if (v != null) {
      return new String(v,StandardCharsets.UTF_8);
    } else {
      return null;
    }
  }

  public Boolean getBooleanByKeyId(String keyId) {
    byte[] v = null;
    try {
      v = get(keyId);
    } catch (Exception e) {
      log.error("could not get key " + keyId + " from redis", e);
    }
    if (v != null) {
      return "1".equals(new String(v,StandardCharsets.UTF_8));
    } else {
      return Boolean.FALSE;
    }
  }
}
