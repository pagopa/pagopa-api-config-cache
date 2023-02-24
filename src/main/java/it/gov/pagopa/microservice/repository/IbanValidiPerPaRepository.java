package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.IbanValidiPerPa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IbanValidiPerPaRepository extends JpaRepository<IbanValidiPerPa, String> {
    @Query(value = "SELECT e FROM IbanValidiPerPa e LEFT JOIN FETCH e.pa")
    List<IbanValidiPerPa> findAllFetching();

    List<IbanValidiPerPa> findByFkPa(Long fkpa);
}
