package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdiMasterValid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CdiMasterValidRepository extends JpaRepository<CdiMasterValid, Long> {
    Optional<CdiMasterValid> findByfkPsp_objId(Long idpsp);

    @Query(value = "SELECT distinct(e) FROM CdiMasterValid e LEFT JOIN FETCH e.cdiDetail d LEFT JOIN FETCH e.fkPsp")
    List<CdiMasterValid> findAllFetching();
}
