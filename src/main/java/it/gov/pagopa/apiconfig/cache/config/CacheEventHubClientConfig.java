package it.gov.pagopa.apiconfig.cache.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheEventHubClientConfig {

  @Bean
  public EventHubProducerClient eventHubProducerClient(
      @Value("${nodo-dei-pagamenti-cache-tx-connection-string}") String connectionString,
      @Value("${nodo-dei-pagamenti-cache-tx-name}") String eventHubName) {
    return new EventHubClientBuilder()
        .connectionString(connectionString, eventHubName)
        .buildProducerClient();
  }
}
