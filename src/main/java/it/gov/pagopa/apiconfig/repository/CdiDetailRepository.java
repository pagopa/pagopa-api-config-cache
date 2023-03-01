package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdiDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiDetailRepository extends JpaRepository<CdiDetail, Long> {

}
