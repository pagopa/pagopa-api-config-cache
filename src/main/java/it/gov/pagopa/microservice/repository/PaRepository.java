package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.Pa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaRepository extends JpaRepository<Pa, Long> {

    @Query("select pa from Pa pa left join fetch pa.ibans")
    List<Pa> findAllFetchingIbans();
}
