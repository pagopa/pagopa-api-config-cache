package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.IntermediariPsp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntermediariPspRepository extends JpaRepository<IntermediariPsp, Long> {

}
