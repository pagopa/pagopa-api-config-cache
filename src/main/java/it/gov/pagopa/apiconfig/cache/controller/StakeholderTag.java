package it.gov.pagopa.apiconfig.cache.controller;

import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller as serving a specific {@link Stakeholder}. The value is read by {@link
 * it.gov.pagopa.apiconfig.cache.config.LoggingAspect} and put in the MDC to enrich the logs.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StakeholderTag {
  Stakeholder value();
}

