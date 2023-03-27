package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.starter.entity.PaStazionePa;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VerifierRepository extends JpaRepository<PaStazionePa, Long> {
  @Query(
      "select distinct(pa.idDominio) from PaStazionePa as paspa left join paspa.pa as pa left join"
          + " paspa.fkStazione as staz where staz.versione = 2")
  List<String> findAllPaForVerifier();
}
