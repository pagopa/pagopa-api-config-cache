package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.BinaryFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BinaryFileRepository extends JpaRepository<BinaryFile, Long> {
}
