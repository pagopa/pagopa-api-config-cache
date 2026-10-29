package it.gov.pagopa.apiconfig.cache.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Initializes the in-memory cache at application startup.
 *
 * <p>The call is made from this separate bean (instead of a {@code @PostConstruct} inside {@link
 * CacheConfigService}) so that it goes through the Spring proxy. This ensures the {@code
 * @Transactional} declared on {@link CacheConfigService} is applied and a Hibernate session stays
 * open while the cache is generated, avoiding {@code LazyInitializationException}.
 */
@Component
@RequiredArgsConstructor
public class CacheStartupInitializer implements ApplicationRunner {

  private final CacheConfigService cacheConfigService;

  @Override
  public void run(ApplicationArguments args) {
    cacheConfigService.preloadKeysFromRedis();
  }
}
