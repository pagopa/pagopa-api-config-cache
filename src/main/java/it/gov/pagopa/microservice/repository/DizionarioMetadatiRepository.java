package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.DizionarioMetadati;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DizionarioMetadatiRepository extends JpaRepository<DizionarioMetadati, Long> {
}
