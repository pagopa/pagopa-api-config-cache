package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdiInformazioniServizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiInformazioniServizioRepository extends JpaRepository<CdiInformazioniServizio, Long> {
}
