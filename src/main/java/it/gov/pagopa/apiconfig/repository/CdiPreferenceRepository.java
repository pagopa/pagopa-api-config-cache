package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CdiPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CdiPreferenceRepository extends JpaRepository<CdiPreference, Long> {
}
