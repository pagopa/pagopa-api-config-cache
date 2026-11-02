package it.gov.pagopa.apiconfig.cache.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CacheEventHubServiceTest {

  @Mock private EventHubProducerClient producer;

  private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

  private static final String ID = "111";
  private static final String VERSION = "GZIP_JSON-test";

  @Test
  void publishEvent_singleBatch_sendsOnce() throws Exception {
    CacheEventHubService service = new CacheEventHubService(producer, objectMapper);

    EventDataBatch batch = mock(EventDataBatch.class);
    when(producer.createBatch()).thenReturn(batch);
    when(batch.tryAdd(any(EventData.class))).thenReturn(true);
    when(batch.getCount()).thenReturn(1);

    service.publishEvent(ID, ZonedDateTime.now(), VERSION);

    verify(producer, times(1)).createBatch();
    verify(producer, times(1)).send(batch);
  }

  @Test
  void publishEvent_batchFull_createsNewBatchAndSendsTwice() throws Exception {
    CacheEventHubService service = new CacheEventHubService(producer, objectMapper);

    EventDataBatch fullBatch = mock(EventDataBatch.class);
    EventDataBatch newBatch = mock(EventDataBatch.class);
    when(producer.createBatch()).thenReturn(fullBatch).thenReturn(newBatch);
    // first batch is full, the fresh batch accepts the event
    when(fullBatch.tryAdd(any(EventData.class))).thenReturn(false);
    when(newBatch.tryAdd(any(EventData.class))).thenReturn(true);
    when(newBatch.getCount()).thenReturn(1);

    service.publishEvent(ID, ZonedDateTime.now(), VERSION);

    verify(producer, times(2)).createBatch();
    // the full (empty) batch is flushed, then the batch holding the event is flushed
    verify(producer, times(1)).send(fullBatch);
    verify(producer, times(1)).send(newBatch);
  }

  @Test
  void publishEvent_eventTooLargeForEmptyBatch_throws() {
    CacheEventHubService service = new CacheEventHubService(producer, objectMapper);

    EventDataBatch batch1 = mock(EventDataBatch.class);
    EventDataBatch batch2 = mock(EventDataBatch.class);
    when(producer.createBatch()).thenReturn(batch1).thenReturn(batch2);
    // the event never fits, even in a brand new empty batch
    when(batch1.tryAdd(any(EventData.class))).thenReturn(false);
    when(batch2.tryAdd(any(EventData.class))).thenReturn(false);

    ZonedDateTime now = ZonedDateTime.now();
    assertThrows(IllegalArgumentException.class, () -> service.publishEvent(ID, now, VERSION));

    verify(producer, times(2)).createBatch();
    // only the first (flushed-empty) batch is sent before the exception is raised
    verify(producer, times(1)).send(batch1);
    verify(producer, never()).send(batch2);
  }
}
