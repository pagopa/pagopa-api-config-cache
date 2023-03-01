package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.Codifiche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodificheRepository extends JpaRepository<Codifiche, Long> {

}
