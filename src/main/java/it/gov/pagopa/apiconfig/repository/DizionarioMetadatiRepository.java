package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.DizionarioMetadati;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DizionarioMetadatiRepository extends JpaRepository<DizionarioMetadati, Long> {
}
