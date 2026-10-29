package it.gov.pagopa.apiconfig.cache.config;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import jakarta.xml.bind.Marshaller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class MarshalerConfig {

  private static final String SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

  @Bean
  public Jaxb2Marshaller marshaler() {
    Jaxb2Marshaller jaxb2Marshaller = new Jaxb2Marshaller();
    jaxb2Marshaller.setPackagesToScan("it.gov.pagopa.apiconfig.cache.imported");
    jaxb2Marshaller.setMarshallerProperties(
        Map.of(
            Marshaller.JAXB_FORMATTED_OUTPUT,
            Boolean.TRUE,
            Marshaller.JAXB_SCHEMA_LOCATION,
            SCHEMA_INSTANCE,
            Marshaller.JAXB_ENCODING,
            StandardCharsets.UTF_8.name()));
    return jaxb2Marshaller;
  }
}
