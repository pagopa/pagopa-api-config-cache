package it.gov.pagopa.apiconfig.cache.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.starter.repository.HealthCheckRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** Verifies the MDC metadata attached to the API invocation logs (success and failure). */
@SpringBootTest
@AutoConfigureMockMvc
class ApiLoggingMdcIntegrationTest {

  @Autowired private MockMvc mvc;

  @MockBean CacheConfigService cacheConfigService;
  @MockBean HealthCheckService healthCheckService;
  @MockBean StakeholderConfigService stakeholderConfigService;
  @MockBean VerifierService verifierService;
  @MockBean HealthCheckRepository healthCheckRepository;
  @MockBean CacheEventHubService cacheEventHubService;

  private final Logger aspectLogger = (Logger) LoggerFactory.getLogger(LoggingAspect.class);
  private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

  @BeforeEach
  void setUp() {
    appender.start();
    aspectLogger.addAppender(appender);
  }

  @AfterEach
  void tearDown() {
    aspectLogger.detachAppender(appender);
  }

  @Test
  void success_containsStakeholder() throws Exception {
    when(verifierService.getPaV2()).thenReturn(List.of());

    mvc.perform(get("/stakeholders/verifier/cache/schemas/v1")).andExpect(status().isOk());

    Map<String, String> mdc = lastEvent("Successful API operation");
    assertThat(mdc)
        .containsEntry(LoggingAspect.METHOD, "cache")
        .containsEntry(LoggingAspect.STAKEHOLDER, "VERIFIER")
        .containsEntry(LoggingAspect.STATUS, "OK")
        .containsEntry(LoggingAspect.CODE, "200")
        .containsKeys(
            LoggingAspect.START_TIME,
            LoggingAspect.OPERATION_ID,
            LoggingAspect.REQUEST_ID,
            LoggingAspect.ARGS,
            LoggingAspect.RESPONSE_TIME)
        .doesNotContainKeys(LoggingAspect.FAULT_CODE, LoggingAspect.FAULT_DETAIL);
  }

  @Test
  void applicationError_containsSameMetadataOfSuccess() throws Exception {
    when(stakeholderConfigService.getVersionId(any(), anyString(), any()))
        .thenThrow(new AppException(AppError.CACHE_ID_NOT_FOUND, "x"));

    mvc.perform(get("/stakeholders/node/cache/schemas/v1/id")).andExpect(status().isNotFound());

    Map<String, String> mdc = lastEvent("Failed API operation");
    assertThat(mdc)
        .containsEntry(LoggingAspect.METHOD, "idV1")
        .containsEntry(LoggingAspect.STAKEHOLDER, "NODE")
        .containsEntry(LoggingAspect.STATUS, "KO")
        .containsEntry(LoggingAspect.CODE, "404")
        .containsKeys(
            LoggingAspect.START_TIME,
            LoggingAspect.OPERATION_ID,
            LoggingAspect.REQUEST_ID,
            LoggingAspect.ARGS,
            LoggingAspect.RESPONSE_TIME,
            LoggingAspect.FAULT_CODE,
            LoggingAspect.FAULT_DETAIL);
  }

  @Test
  void bindingError_containsInvocationMetadata() throws Exception {
    mvc.perform(get("/stakeholders/wisp/cache/schemas/v1").param("refresh", "notABoolean"))
        .andExpect(status().isBadRequest());

    Map<String, String> mdc = lastEvent("Failed API operation");
    assertThat(mdc)
        .containsEntry(LoggingAspect.METHOD, "cache")
        .containsEntry(LoggingAspect.STAKEHOLDER, "WISP")
        .containsEntry(LoggingAspect.STATUS, "KO")
        .containsEntry(LoggingAspect.CODE, "400")
        .containsKeys(
            LoggingAspect.START_TIME,
            LoggingAspect.OPERATION_ID,
            LoggingAspect.REQUEST_ID,
            LoggingAspect.RESPONSE_TIME,
            LoggingAspect.FAULT_CODE,
            LoggingAspect.FAULT_DETAIL);
  }

  private Map<String, String> lastEvent(String messagePrefix) {
    return appender.list.stream()
        .filter(e -> e.getFormattedMessage().startsWith(messagePrefix))
        .reduce((first, second) -> second)
        .map(ILoggingEvent::getMDCPropertyMap)
        .orElseThrow(() -> new AssertionError("No log event starting with: " + messagePrefix));
  }
}

