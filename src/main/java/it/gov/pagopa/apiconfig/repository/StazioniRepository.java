package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.Stazioni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StazioniRepository extends JpaRepository<Stazioni, Long> {
    @Query(value = "SELECT e FROM Stazioni e LEFT JOIN FETCH e.intermediarioPa")
    List<Stazioni> findAllFetching();
}
