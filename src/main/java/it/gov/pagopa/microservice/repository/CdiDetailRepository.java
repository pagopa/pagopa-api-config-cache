package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdiDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiDetailRepository extends JpaRepository<CdiDetail, Long> {

}
