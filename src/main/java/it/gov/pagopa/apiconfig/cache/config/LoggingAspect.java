package it.gov.pagopa.apiconfig.cache.config;

import it.gov.pagopa.apiconfig.cache.controller.StakeholderTag;
import it.gov.pagopa.apiconfig.cache.model.ProblemJson;
import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import it.gov.pagopa.apiconfig.cache.util.CommonUtility;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

  public static final String START_TIME = "startTime";
  public static final String METHOD = "method";
  public static final String STATUS = "status";
  public static final String CODE = "httpCode";
  public static final String RESPONSE_TIME = "responseTime";
  public static final String FAULT_CODE = "faultCode";
  public static final String FAULT_DETAIL = "faultDetail";
  public static final String REQUEST_ID = "requestId";
  public static final String OPERATION_ID = "operationId";
  public static final String ARGS = "args";
  public static final String STAKEHOLDER = "stakeholder";

  /** MDC keys added by this aspect for each API invocation (shared by success and failure logs). */
  private static final List<String> API_INVOCATION_MDC_KEYS =
      List.of(
          METHOD, START_TIME, OPERATION_ID, ARGS, STAKEHOLDER, STATUS, CODE, RESPONSE_TIME);

  private final String name;
  private final String version;
  private final String environment;
  private final HttpServletResponse httpResponse;

  public LoggingAspect(
      @Value("${info.application.name}") String name,
      @Value("${info.application.version}") String version,
      @Value("${info.properties.environment}") String environment,
      HttpServletResponse httpResponse) {
    this.name = name;
    this.version = version;
    this.environment = environment;
    this.httpResponse = httpResponse;
  }

  @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
  public void restController() {
    // all rest controllers
  }

  @Pointcut("@within(org.springframework.stereotype.Repository)")
  public void repository() {
    // all repository methods
  }

  @Pointcut("@within(org.springframework.stereotype.Service)")
  public void service() {
    // all service methods
  }

  /** Log essential info of application during the startup. */
  @PostConstruct
  public void logStartup() {
    log.info("-> Starting {} version {} - environment {}", name, version, environment);
  }

  @Around(value = "restController()")
  public Object logApiInvocation(ProceedingJoinPoint joinPoint) throws Throwable {
    MDC.put(METHOD, joinPoint.getSignature().getName());
    MDC.put(START_TIME, String.valueOf(System.currentTimeMillis()));
    MDC.put(OPERATION_ID, UUID.randomUUID().toString());
    if (MDC.get(REQUEST_ID) == null) {
      var requestId = UUID.randomUUID().toString();
      MDC.put(REQUEST_ID, requestId);
    }
    getStakeholder(joinPoint).ifPresent(stakeholder -> MDC.put(STAKEHOLDER, stakeholder));
    Map<String, String> params = getParams(joinPoint);
    MDC.put(ARGS, params.toString());

    log.debug("Invoking API operation {}", joinPoint.getSignature().getName());

    // In case of exception, the MDC is NOT cleaned here: the exception is handled afterwards by
    // the ErrorHandler and the related log (see trowingApiInvocation) must contain the same
    // metadata of the successful one. The cleanup is performed there.
    Object result = joinPoint.proceed();

    MDC.put(STATUS, "OK");
    MDC.put(CODE, String.valueOf(httpResponse.getStatus()));
    MDC.put(RESPONSE_TIME, getExecutionTime());
    log.info("Successful API operation {}", joinPoint.getSignature().getName());
    clearApiInvocationMdc();
    return result;
  }

  @AfterReturning(value = "execution(* *..exception.ErrorHandler.*(..))", returning = "result")
  public void trowingApiInvocation(JoinPoint joinPoint, ResponseEntity<ProblemJson> result) {
    MDC.put(STATUS, "KO");
    MDC.put(CODE, String.valueOf(result.getStatusCode().value()));
    MDC.put(RESPONSE_TIME, getExecutionTime());
    MDC.put(FAULT_CODE, getTitle(result));
    MDC.put(FAULT_DETAIL, getDetail(result));
    log.info("Failed API operation {} - error: {}", MDC.get(METHOD), result);
    MDC.remove(FAULT_CODE);
    MDC.remove(FAULT_DETAIL);
    clearApiInvocationMdc();
  }

  @Around(value = "repository() || service()")
  public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
    //    Map<String, String> params = getParams(joinPoint);
    log.debug("Call method {}", joinPoint.getSignature().toShortString());
    Object result = joinPoint.proceed();
    log.debug("Return method {} - result: {}", joinPoint.getSignature().toShortString(), result);
    return result;
  }

  /**
   * Remove from the MDC only the keys added by this aspect for the API invocation. The keys owned
   * by other components (e.g. {@code requestId} set by {@link RequestFilter}) are left untouched.
   */
  private static void clearApiInvocationMdc() {
    API_INVOCATION_MDC_KEYS.forEach(MDC::remove);
  }

  private static Optional<String> getStakeholder(JoinPoint joinPoint) {
    return Optional.ofNullable(joinPoint.getTarget())
            .map(AopUtils::getTargetClass)
            .map(clazz -> AnnotationUtils.findAnnotation(clazz, StakeholderTag.class))
            .map(StakeholderTag::value)
            .map(Stakeholder::name);
  }

  private static String getDetail(ResponseEntity<ProblemJson> result) {
    return Optional.ofNullable(result)
        .map(ResponseEntity::getBody)
        .map(ProblemJson::getDetail)
        .orElse(null);
  }

  private static String getTitle(ResponseEntity<ProblemJson> result) {
    return Optional.ofNullable(result)
        .map(ResponseEntity::getBody)
        .map(ProblemJson::getTitle)
        .orElse(null);
  }

  public static String getExecutionTime() {
    String startTime = MDC.get(START_TIME);
    if (startTime != null) {
      long endTime = System.currentTimeMillis();
      long executionTime = endTime - Long.parseLong(startTime);
      return String.valueOf(executionTime);
    }
    return "-";
  }

  private static Map<String, String> getParams(ProceedingJoinPoint joinPoint) {
    CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
    Map<String, String> params = new HashMap<>();
    int i = 0;
    for (var paramName : codeSignature.getParameterNames()) {
      params.put(paramName, CommonUtility.deNull(joinPoint.getArgs()[i++]));
    }
    return params;
  }
}
