package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.InformativePaFasce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InformativePaFasceRepository extends JpaRepository<InformativePaFasce, Long> {
}
