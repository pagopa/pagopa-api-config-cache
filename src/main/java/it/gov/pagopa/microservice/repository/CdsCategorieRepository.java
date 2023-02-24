package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdsCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CdsCategorieRepository extends JpaRepository<CdsCategoria, String> {
}
