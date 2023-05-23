package it.gov.pagopa.apiconfig.cache.redis;

import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisRepository {

  @Autowired
  @Qualifier("configData")
  private RedisTemplate<String, ConfigDataV1> redisTemplate;
  @Autowired
  @Qualifier("object")
  private RedisTemplate<String, Object> redisTemplateObj;

  public void save(String key, ConfigDataV1 value, long ttl) {
    redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(ttl));
  }
  public void save(String key, Object value, long ttl) {
    redisTemplateObj.opsForValue().set(key, value, Duration.ofMinutes(ttl));
  }

  public ConfigDataV1 getConfigDataV1(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  public Object get(String key) {
    return redisTemplateObj.opsForValue().get(key);
  }

  public void remove(String key) {
    redisTemplateObj.delete(key);
  }

  @Async
  public void pushToRedisAsync(String key, String keyId, ConfigDataV1 configData) {
    try {
      log.info("saving {} on redis", key);
      save(key, configData, 1440);
      save(keyId, configData.getVersion(), 1440);
      log.info("saved {} on redis,id {}", key, configData.getVersion());
    } catch (Exception e) {
      log.error("could not save on redis", e);
    }
  }

  @Async
  public void pushToRedisAsync(String key, ConfigDataV1 object) {
    try {
      log.info("saving {} on redis", key);
      save(key, object, 1440);
      log.info("saved {} on redis", key);
    } catch (Exception e) {
      log.error("could not save " + key + "on redis", e);
    }
  }

  public String getStringByKeyId(String keyId) {
    Object v = null;
    try {
      v = get(keyId);
    } catch (Exception e) {
      log.error("could not get key " + keyId + " from redis", e);
    }
    if (v != null) {
      return (String) v;
    } else {
      return null;
    }
  }

  public Boolean getBooleanByKeyId(String keyId) {
    Object v = null;
    try {
      v = get(keyId);
    } catch (Exception e) {
      log.error("could not get key " + keyId + " from redis", e);
    }
    if (v != null) {
      return (Boolean) v;
    } else {
      return null;
    }
  }
}
