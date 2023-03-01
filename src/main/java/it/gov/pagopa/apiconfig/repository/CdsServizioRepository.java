package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdsServizio;
import it.gov.pagopa.apiconfig.entity.Stazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface CdsServizioRepository extends JpaRepository<CdsServizio, Long> {
  @Query(value = "SELECT e FROM CdsServizio e LEFT JOIN FETCH e.categoria")
  List<CdsServizio> findAllFetching();
}
