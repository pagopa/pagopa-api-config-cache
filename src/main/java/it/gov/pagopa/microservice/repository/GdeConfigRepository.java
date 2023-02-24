package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.GdeConfig;
import it.gov.pagopa.microservice.entity.GdeConfigPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GdeConfigRepository extends JpaRepository<GdeConfig, GdeConfigPk> {
}
