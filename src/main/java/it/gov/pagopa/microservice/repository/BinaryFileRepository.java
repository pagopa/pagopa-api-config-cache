package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.BinaryFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryFileRepository extends JpaRepository<BinaryFile, Long> {
}
