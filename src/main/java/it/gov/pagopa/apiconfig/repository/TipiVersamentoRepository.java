package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.TipiVersamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipiVersamentoRepository extends JpaRepository<TipiVersamento, Long> {

}
