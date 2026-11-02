package it.gov.pagopa.apiconfig.cache.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.gov.pagopa.apiconfig.cache.controller.stakeholders.NodeCacheController;
import it.gov.pagopa.apiconfig.cache.controller.stakeholders.VerifierCacheController;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

class ApiLoggingInterceptorTest {

  private final ApiLoggingInterceptor interceptor = new ApiLoggingInterceptor();
  private final MockHttpServletRequest request = new MockHttpServletRequest();
  private final MockHttpServletResponse response = new MockHttpServletResponse();

  @AfterEach
  void tearDown() {
    MDC.clear();
  }

  @Test
  void preHandle_setsMetadataFromTaggedController() throws Exception {
    HandlerMethod handler =
        new HandlerMethod(
            new VerifierCacheController(Mockito.mock(VerifierService.class)), "cache");

    assertTrue(interceptor.preHandle(request, response, handler));

    assertEquals("cache", MDC.get(LoggingAspect.METHOD));
    assertEquals("VERIFIER", MDC.get(LoggingAspect.STAKEHOLDER));
    assertNotNull(MDC.get(LoggingAspect.START_TIME));
    assertNotNull(MDC.get(LoggingAspect.OPERATION_ID));
  }

  @Test
  void preHandle_resolvesStakeholderOnInheritedHandlerMethod() throws Exception {
    NodeCacheController controller = Mockito.mock(NodeCacheController.class);
    HandlerMethod handler =
        new HandlerMethod(controller, NodeCacheController.class.getMethod("idV1"));

    interceptor.preHandle(request, response, handler);

    assertEquals("NODE", MDC.get(LoggingAspect.STAKEHOLDER));
  }

  @Test
  void preHandle_ignoresNonHandlerMethod() {
    assertTrue(interceptor.preHandle(request, response, new Object()));
    assertNull(MDC.get(LoggingAspect.METHOD));
    assertNull(MDC.get(LoggingAspect.STAKEHOLDER));
  }

  @Test
  void afterCompletion_removesOnlyApiInvocationKeys() {
    MDC.put(LoggingAspect.REQUEST_ID, "req-1");
    ApiLoggingInterceptor.API_INVOCATION_MDC_KEYS.forEach(key -> MDC.put(key, "x"));

    interceptor.afterCompletion(request, response, new Object(), null);

    ApiLoggingInterceptor.API_INVOCATION_MDC_KEYS.forEach(key -> assertNull(MDC.get(key)));
    assertEquals("req-1", MDC.get(LoggingAspect.REQUEST_ID));
  }
}

