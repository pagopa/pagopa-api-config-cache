package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.CanaleTipoVersamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface CanaleTipoVersamentoRepository extends JpaRepository<CanaleTipoVersamento, Long> {

}
