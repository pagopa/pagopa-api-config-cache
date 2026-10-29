package it.gov.pagopa.apiconfig.cache.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CacheEventHubService {

  private final EventHubProducerClient producer;
  private final ObjectMapper objectMapper;

  public CacheEventHubService(EventHubProducerClient producer, ObjectMapper objectMapper) {
    this.producer = producer;
    this.objectMapper = objectMapper;
  }

  // https://learn.microsoft.com/en-us/azure/event-hubs/event-hubs-java-get-started-send?tabs=connection-string%2Croles-azure-portal#add-code-to-publish-events-to-the-event-hub
  public void publishEvent(String id, ZonedDateTime now, String version)
      throws JsonProcessingException {
    List<EventData> allEvents = buildEventData(id, now, version);
    EventDataBatch eventDataBatch = producer.createBatch();
    for (EventData eventData : allEvents) {
      if (!eventDataBatch.tryAdd(eventData)) {
        producer.send(eventDataBatch);
        eventDataBatch = producer.createBatch();
        if (!eventDataBatch.tryAdd(eventData)) {
          throw new IllegalArgumentException(
              "Event is too large for an empty batch. Max size: "
                  + eventDataBatch.getMaxSizeInBytes());
        }
      }
    }
    if (eventDataBatch.getCount() > 0) {
      producer.send(eventDataBatch);
    }
  }

  private List<EventData> buildEventData(String id, ZonedDateTime now, String version)
      throws JsonProcessingException {
    Map<String, Object> e = new HashMap<>();
    e.put(Constants.VERSION, id);
    e.put(Constants.TIMESTAMP, now);
    e.put(Constants.CACHE_VERSION, version);
    return List.of(new EventData(objectMapper.writeValueAsString(e)));
  }
}
