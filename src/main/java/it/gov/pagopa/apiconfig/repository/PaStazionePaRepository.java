package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.PaStazionePa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaStazionePaRepository extends JpaRepository<PaStazionePa, Long> {

    @Query("select distinct(paspa) from PaStazionePa paspa left join fetch paspa.pa left join fetch paspa.stazione")
    List<PaStazionePa> findAllFetching();

}
