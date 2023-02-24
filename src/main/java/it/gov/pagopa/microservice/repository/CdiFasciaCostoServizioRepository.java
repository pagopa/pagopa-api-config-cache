package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdiFasciaCostoServizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiFasciaCostoServizioRepository extends JpaRepository<CdiFasciaCostoServizio, Long> {
}
