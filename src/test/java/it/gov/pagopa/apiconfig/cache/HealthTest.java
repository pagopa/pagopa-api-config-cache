package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.starter.repository.HealthCheckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HealthTest {

  @Mock private HealthCheckRepository healthCheckRepository;

  @InjectMocks private HealthCheckService healthCheckService;

  @BeforeEach
  void setUp() {
    when(healthCheckRepository.health()).thenReturn(Optional.of(Boolean.TRUE));
  }

  @Test
  void getCacheV1() {
    assertThat(healthCheckService.checkDatabaseConnection());
  }
}
