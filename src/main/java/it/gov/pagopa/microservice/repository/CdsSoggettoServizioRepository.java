package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdsSoggettoServizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CdsSoggettoServizioRepository extends JpaRepository<CdsSoggettoServizio, Long> {
}
