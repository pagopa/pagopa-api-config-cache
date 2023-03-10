package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdiMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CdiMasterRepository extends JpaRepository<CdiMaster, Long> {

}
