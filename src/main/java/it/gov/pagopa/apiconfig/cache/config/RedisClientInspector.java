package it.gov.pagopa.apiconfig.cache.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class RedisClientInspector {

    /**
     * This class is used to inspect the Redis client configuration.
     * It logs the command timeout and socket connect timeout settings.
     * It is useful for debugging and ensuring that the Redis client is configured correctly.
     */

    private final LettuceConnectionFactory connectionFactory;

    public RedisClientInspector(LettuceConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @PostConstruct
    public void inspect() {
        var clientConfig = connectionFactory.getClientConfiguration();

        log.info("Redis command timeout: " + clientConfig.getCommandTimeout());

        clientConfig.getClientOptions().ifPresent(options -> {
            var socketOptions = options.getSocketOptions();
            log.info("Redis Socket connect timeout: " + socketOptions.getConnectTimeout());
        });
    }
}

