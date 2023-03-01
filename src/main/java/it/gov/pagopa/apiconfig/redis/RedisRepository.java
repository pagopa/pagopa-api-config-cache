package it.gov.pagopa.apiconfig.redis;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisRepository {
  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  public void save(String key, Object value, long ttl) {
    redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(ttl));
  }
  public Object get(String key) {
    return redisTemplate.opsForValue().get(key);
  }
}

