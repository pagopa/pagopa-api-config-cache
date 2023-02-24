package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.Codifiche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CodificheRepository extends JpaRepository<Codifiche, Long> {

}
