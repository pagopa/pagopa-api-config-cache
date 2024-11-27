package it.gov.pagopa.apiconfig.cache.repository;

import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerifierRepository extends JpaRepository<PaStazionePa, Long> {
  @Query(
      "select distinct(pa.idDominio) from PaStazionePa as paspa left join paspa.pa as pa left join"
          + " paspa.fkStazione as staz where staz.versione = 2")
  List<String> findAllPaForVerifier();
}
