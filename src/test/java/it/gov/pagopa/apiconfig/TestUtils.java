package it.gov.pagopa.apiconfig;

import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import it.gov.pagopa.apiconfig.starter.entity.DizionarioMetadati;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public class TestUtils {

  public static List<ConfigurationKeys> mockConfigurationKeys = Arrays.asList(
      ConfigurationKeys.builder()
          .configCategory("configCategory")
          .configKey("configKey")
          .configValue("configValue")
          .configDescription("configDescription").build());

  public static List<DizionarioMetadati> mockMetadataDicts = Arrays.asList(
      DizionarioMetadati.builder()
          .key("key")
          .startDate(ZonedDateTime.now())
          .endDate(ZonedDateTime.now())
          .description("description").build());

  public static List<Pa> pas = Arrays.asList(Pa.builder()
      .description("description")
      .enabled(true)
      .idDominio("idDominio")
      .pagamentoPressoPsp(true)
      .build());

  public static List<Psp> psps = Arrays.asList(Psp.builder()
      .idPsp("idPsp")
      .build());

}
