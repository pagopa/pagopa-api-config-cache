package it.gov.pagopa.apiconfig.cache.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheEventHubService {

    @Value("${nodo-dei-pagamenti-cache-tx-connection-string}")
    private String connectionString;

    @Value("${nodo-dei-pagamenti-cache-tx-name}")
    private String eventHubName;

    private EventHubProducerClient producer;
    @Autowired
    private ObjectMapper om;

    private EventHubProducerClient getProducer(){
        if(producer==null){
            producer = new EventHubClientBuilder()
                    .connectionString(connectionString, eventHubName)
                    .buildProducerClient();
        }
        return producer;
    }

    public void publishEvent(String id, ZonedDateTime now, String version) throws JsonProcessingException {
        Map e = new HashMap();
        e.put(Constants.version,id);
        e.put(Constants.timestamp,now);
        e.put(Constants.cacheVersion,version);
        List<EventData> allEvents = Arrays.asList(new EventData(om.writeValueAsString(e)));
        EventDataBatch eventDataBatch = getProducer().createBatch();
        for (EventData eventData : allEvents) {
            if (!eventDataBatch.tryAdd(eventData)) {
                getProducer().send(eventDataBatch);
                eventDataBatch = getProducer().createBatch();
                if (!eventDataBatch.tryAdd(eventData)) {
                    throw new IllegalArgumentException("Event is too large for an empty batch. Max size: "
                            + eventDataBatch.getMaxSizeInBytes());
                }
            }
        }
        if (eventDataBatch.getCount() > 0) {
            producer.send(eventDataBatch);
        }

    }

    @PreDestroy
    private void preDestroy(){
        getProducer().close();
    }

}
