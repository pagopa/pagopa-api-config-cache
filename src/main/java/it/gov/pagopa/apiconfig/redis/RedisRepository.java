package it.gov.pagopa.apiconfig.redis;

import it.gov.pagopa.apiconfig.model.node.v1.ConfigDataV1;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisRepository {

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  public void save(String key, Object value, long ttl) {
    redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(ttl));
  }

  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }

  @Async
  public void pushToRedisAsync(String key,String keyId,ConfigDataV1 configData) {
    try {
      log.info("saving on redis");
      save(key, configData, 1440);
      save(keyId, configData.getVersion(), 1440);
      log.info("saved on redis version " + configData.getVersion());
    } catch (Exception e) {
      log.error("could not save on redis", e);
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
}

