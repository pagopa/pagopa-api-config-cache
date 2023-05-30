package it.gov.pagopa.apiconfig.cache;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

public class TestValueOperation implements ValueOperations {

  Object value = new Object();

  @Override
  public void set(Object key, Object value) {
    this.value = value;
  }

  @Override
  public void set(Object key, Object value, long timeout, TimeUnit unit) {

  }

  @Override
  public Boolean setIfAbsent(Object key, Object value) {
    return null;
  }

  @Override
  public Boolean setIfAbsent(Object key, Object value, long timeout, TimeUnit unit) {
    return null;
  }

  @Override
  public Boolean setIfPresent(Object key, Object value) {
    return null;
  }

  @Override
  public Boolean setIfPresent(Object key, Object value, long timeout, TimeUnit unit) {
    return null;
  }

  @Override
  public void multiSet(Map map) {

  }

  @Override
  public Boolean multiSetIfAbsent(Map map) {
    return null;
  }

  @Override
  public Object get(Object key) {
    return value;
  }

  @Override
  public Object getAndDelete(Object key) {
    return null;
  }

  @Override
  public Object getAndExpire(Object key, long timeout, TimeUnit unit) {
    return null;
  }

  @Override
  public Object getAndExpire(Object key, Duration timeout) {
    return null;
  }

  @Override
  public Object getAndPersist(Object key) {
    return null;
  }

  @Override
  public Object getAndSet(Object key, Object value) {
    return null;
  }

  @Override
  public List multiGet(Collection keys) {
    return null;
  }

  @Override
  public Long increment(Object key) {
    return null;
  }

  @Override
  public Long increment(Object key, long delta) {
    return null;
  }

  @Override
  public Double increment(Object key, double delta) {
    return null;
  }

  @Override
  public Long decrement(Object key) {
    return null;
  }

  @Override
  public Long decrement(Object key, long delta) {
    return null;
  }

  @Override
  public Integer append(Object key, String value) {
    return null;
  }

  @Override
  public String get(Object key, long start, long end) {
    return null;
  }

  @Override
  public void set(Object key, Object value, long offset) {

  }

  @Override
  public Long size(Object key) {
    return null;
  }

  @Override
  public Boolean setBit(Object key, long offset, boolean value) {
    return null;
  }

  @Override
  public Boolean getBit(Object key, long offset) {
    return null;
  }

  @Override
  public List<Long> bitField(Object key, BitFieldSubCommands subCommands) {
    return null;
  }

  @Override
  public RedisOperations getOperations() {
    return null;
  }
}
