package it.gov.pagopa.apiconfig.cache.service;

import it.gov.pagopa.apiconfig.starter.repository.HealthCheckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class HealthCheckService {

  private final HealthCheckRepository healthCheckRepository;

  public HealthCheckService(HealthCheckRepository healthCheckRepository) {
    this.healthCheckRepository = healthCheckRepository;
  }

  public boolean checkDatabaseConnection() {
    try {
      return healthCheckRepository.health().isPresent();
    } catch (DataAccessResourceFailureException e) {
      log.error("Database connection check failed", e);
      return false;
    }
  }
}
