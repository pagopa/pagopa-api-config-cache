package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.PspCanaleTipoVersamentoCanale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface PspCanaleTipoVersamentoRepository extends JpaRepository<PspCanaleTipoVersamentoCanale, Long> {
    @Query("select distinct(c) from PspCanaleTipoVersamentoCanale c left join fetch c.psp left join fetch c.canale left join fetch c.tipoVersamento")
    List<PspCanaleTipoVersamentoCanale> findAllFetching();
}
