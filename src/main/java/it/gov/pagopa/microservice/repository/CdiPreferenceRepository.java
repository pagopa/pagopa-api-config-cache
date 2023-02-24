package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.CdiPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiPreferenceRepository extends JpaRepository<CdiPreference, Long> {
}
