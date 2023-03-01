package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdsServizio;
import it.gov.pagopa.apiconfig.entity.CdsSoggettoServizio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CdsSoggettoServizioRepository extends JpaRepository<CdsSoggettoServizio, Long> {
  @Query(value = "SELECT e FROM CdsSoggettoServizio e LEFT JOIN FETCH e.soggetto LEFT JOIN FETCH e.servizio")
  List<CdsSoggettoServizio> findAllFetching();
}
