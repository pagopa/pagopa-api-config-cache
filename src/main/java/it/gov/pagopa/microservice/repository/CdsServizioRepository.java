package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdsServizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CdsServizioRepository extends JpaRepository<CdsServizio, Long> {
}
