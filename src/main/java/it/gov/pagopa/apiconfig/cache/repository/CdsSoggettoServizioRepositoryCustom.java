package it.gov.pagopa.apiconfig.cache.repository;

import it.gov.pagopa.apiconfig.cache.entity.CdsSoggettoServizioCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CdsSoggettoServizioRepositoryCustom extends JpaRepository<CdsSoggettoServizioCustom, Long> {
  @Query("SELECT e FROM CdsSoggettoServizio e LEFT JOIN FETCH e.soggetto LEFT JOIN FETCH e.servizio LEFT JOIN FETCH e.stazione")
  List<CdsSoggettoServizioCustom> findAllFetchingStations();
}

