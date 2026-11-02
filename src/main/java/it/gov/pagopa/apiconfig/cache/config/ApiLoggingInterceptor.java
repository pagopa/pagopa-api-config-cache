package it.gov.pagopa.apiconfig.cache.config;

import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.ARGS;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.CODE;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.FAULT_CODE;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.FAULT_DETAIL;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.METHOD;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.OPERATION_ID;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.REQUEST_ID;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.RESPONSE_TIME;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.STAKEHOLDER;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.START_TIME;
import static it.gov.pagopa.apiconfig.cache.config.LoggingAspect.STATUS;

import it.gov.pagopa.apiconfig.cache.controller.StakeholderTag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Owns the lifecycle of the API invocation metadata in the MDC.
 *
 * <ul>
 *   <li>{@link #preHandle} runs after the handler resolution but before the argument binding, so
 *       the metadata are available also for the errors raised during the binding (e.g. type
 *       mismatch, missing parameters).
 *   <li>{@link #afterCompletion} is always invoked, after the {@code ErrorHandler} too, so it is the
 *       single point where the keys are removed.
 * </ul>
 */
public class ApiLoggingInterceptor implements HandlerInterceptor {

  /** MDC keys added for each API invocation (by this interceptor and by {@link LoggingAspect}). */
  static final List<String> API_INVOCATION_MDC_KEYS =
      List.of(
          METHOD,
          START_TIME,
          OPERATION_ID,
          STAKEHOLDER,
          ARGS,
          STATUS,
          CODE,
          RESPONSE_TIME,
          FAULT_CODE,
          FAULT_DETAIL);

  @Override
  public boolean preHandle(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler) {
    if (handler instanceof HandlerMethod handlerMethod) {
      MDC.put(METHOD, handlerMethod.getMethod().getName());
      MDC.put(START_TIME, String.valueOf(System.currentTimeMillis()));
      MDC.put(OPERATION_ID, UUID.randomUUID().toString());
      if (MDC.get(REQUEST_ID) == null) {
        MDC.put(REQUEST_ID, UUID.randomUUID().toString());
      }
      StakeholderTag tag =
          AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), StakeholderTag.class);
      if (tag != null) {
        MDC.put(STAKEHOLDER, tag.value().name());
      }
    }
    return true;
  }

  @Override
  public void afterCompletion(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull Object handler,
      @Nullable Exception ex) {
    API_INVOCATION_MDC_KEYS.forEach(MDC::remove);
  }
}

