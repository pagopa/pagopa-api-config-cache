package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.Stazioni;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StazioniRepository extends JpaRepository<Stazioni, Long> {
    @Query(value = "SELECT e FROM Stazioni e LEFT JOIN FETCH e.intermediarioPa")
    List<Stazioni> findAllFetching();
}
