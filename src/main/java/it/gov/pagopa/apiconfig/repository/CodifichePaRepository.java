package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CodifichePa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CodifichePaRepository extends JpaRepository<CodifichePa, Long> {

    @Query("select cpa from CodifichePa cpa left join fetch cpa.fkCodifica")
    List<CodifichePa> findAllFetching();
}
