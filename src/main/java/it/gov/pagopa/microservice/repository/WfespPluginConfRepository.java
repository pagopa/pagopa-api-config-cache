package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.WfespPluginConf;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WfespPluginConfRepository extends JpaRepository<WfespPluginConf, Long> {

}
