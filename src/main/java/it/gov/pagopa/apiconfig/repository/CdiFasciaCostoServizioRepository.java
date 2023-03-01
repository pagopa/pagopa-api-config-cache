package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdiFasciaCostoServizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiFasciaCostoServizioRepository extends JpaRepository<CdiFasciaCostoServizio, Long> {
}
