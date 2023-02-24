package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.Canali;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CanaliRepository extends JpaRepository<Canali, Long> {
    @Query(value = "SELECT c FROM Canali c LEFT JOIN FETCH c.fkIntermediarioPsp")
    List<Canali> findAllFetching();
}
