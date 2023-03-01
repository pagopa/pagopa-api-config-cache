package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.Pa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaRepository extends JpaRepository<Pa, Long> {

    @Query("select pa from Pa pa left join fetch pa.ibans")
    List<Pa> findAllFetchingIbans();
}
