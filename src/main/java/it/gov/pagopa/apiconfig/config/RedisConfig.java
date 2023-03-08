package it.gov.pagopa.apiconfig.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.redis.host}")
  private String redisHost;

  @Value("${spring.redis.port}")
  private int redisPort;

  @Value("${spring.redis.pwd}")
  private String redisPwd;

  @Bean
  public ObjectMapper objectMapper() {
    final var objectMapper = new ObjectMapper().findAndRegisterModules();
    objectMapper.setVisibility(PropertyAccessor.ALL,  JsonAutoDetect.Visibility.ANY);
    return objectMapper;
  }

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration(redisHost, redisPort);
    redisConfiguration.setPassword(redisPwd);
    LettuceClientConfiguration lettuceConfig = LettuceClientConfiguration.builder()
        .useSsl()
        .build();
    return new LettuceConnectionFactory(redisConfiguration,lettuceConfig);
  }

  @Bean
  public RedisTemplate<String, Object> redisObjectTemplate(final LettuceConnectionFactory connectionFactory,ObjectMapper objectMapper) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setKeySerializer(new StringRedisSerializer());
    final var jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
    jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.setConnectionFactory(connectionFactory);
    return template;
  }

}
