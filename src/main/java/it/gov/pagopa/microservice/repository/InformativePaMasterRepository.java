package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.InformativePaDetail;
import it.gov.pagopa.microservice.entity.InformativePaMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@SuppressWarnings("java:S100") // Disabled naming convention rule for method name to use Spring Data interface
@Repository
public interface InformativePaMasterRepository extends JpaRepository<InformativePaMaster, Long> {

    @Query(value = "SELECT i FROM InformativePaMaster i where fkPa.objId=:idPa and dataInizioValidita <= now()" +
            "order by dataInizioValidita desc,id desc")
    List<InformativePaMaster> findByfkPa_objId(Long idPa);
}
