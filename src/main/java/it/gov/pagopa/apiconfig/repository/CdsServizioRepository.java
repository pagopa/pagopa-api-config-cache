package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdsServizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CdsServizioRepository extends JpaRepository<CdsServizio, Long> {
}
