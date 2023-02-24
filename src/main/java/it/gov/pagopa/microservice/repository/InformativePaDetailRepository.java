package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.InformativePaDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformativePaDetailRepository extends JpaRepository<InformativePaDetail, Long> {

    @Query(value = "SELECT distinct(i) FROM InformativePaDetail i LEFT JOIN FETCH i.fasce where i.fkInformativaPaMaster.id=:idMas")
    List<InformativePaDetail> findByfkInformativaPaMaster_id(Long idMas);
}
