package it.gov.pagopa.apiconfig.cache.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.pwd}")
    private String redisPwd;
    @Value("${spring.data.redis.commandTimeout}")
    private Duration redisCommandTimeout;
    @Value("${spring.data.redis.socketTimeout}")
    private Duration redisSocketTimeout;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfiguration =
                new RedisStandaloneConfiguration(redisHost, redisPort);
        redisConfiguration.setPassword(redisPwd);
        LettuceClientConfiguration lettuceConfig =
                LettuceClientConfiguration.builder()
                        .commandTimeout(redisCommandTimeout)
                        .clientOptions(
                                ClientOptions.builder()
                                        .socketOptions(
                                                SocketOptions.builder().connectTimeout(redisSocketTimeout).build())
                                        .build())
                        .useSsl().build();
        return new LettuceConnectionFactory(redisConfiguration, lettuceConfig);
    }

    @Bean
    public RedisTemplate<String, byte[]> redisObjectTemplate(final LettuceConnectionFactory connectionFactory) {
        RedisTemplate<String, byte[]> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
