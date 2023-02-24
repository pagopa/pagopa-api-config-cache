package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdsSoggetto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CdsSoggettoRepository extends JpaRepository<CdsSoggetto, Long> {
}
