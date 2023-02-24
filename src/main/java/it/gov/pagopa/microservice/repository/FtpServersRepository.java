package it.gov.pagopa.microservice.repository;

import it.gov.pagopa.microservice.entity.FtpServers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FtpServersRepository extends JpaRepository<FtpServers, Long> {
}
