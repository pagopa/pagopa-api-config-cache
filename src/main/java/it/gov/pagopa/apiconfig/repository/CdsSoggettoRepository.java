package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdsSoggetto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CdsSoggettoRepository extends JpaRepository<CdsSoggetto, Long> {
}
