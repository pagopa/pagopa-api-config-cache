package it.gov.pagopa.apiconfig.cache.repository;

import it.gov.pagopa.apiconfig.starter.entity.CdsSoggettoServizio;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;import org.springframework.stereotype.Repository;

@Repository
public interface CdsSoggettoServizioRepositoryCustom extends JpaRepository<CdsSoggettoServizio, Long> {
  @Query("SELECT e FROM CdsSoggettoServizio e LEFT JOIN FETCH e.soggetto LEFT JOIN FETCH e.servizio LEFT JOIN FETCH e.stazione")
  List<CdsSoggettoServizio> findAllFetchingStations();
}

