package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.Pdd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PddRepository extends JpaRepository<Pdd, Long> {
}
