package it.gov.pagopa.apiconfig.repository;

import it.gov.pagopa.apiconfig.entity.GdeConfig;
import it.gov.pagopa.apiconfig.entity.GdeConfigPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GdeConfigRepository extends JpaRepository<GdeConfig, GdeConfigPk> {
}
