package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertIntermediariPaToBrokerDetails
    implements Converter<IntermediariPa, BrokerCreditorInstitution> {

  @Override
  public BrokerCreditorInstitution convert(
      MappingContext<IntermediariPa, BrokerCreditorInstitution> context) {
    IntermediariPa source = context.getSource();
    return BrokerCreditorInstitution.builder()
        .enabled(source.getEnabled())
        .brokerCode(source.getIdIntermediarioPa())
        .description(source.getCodiceIntermediario())
        .extendedFaultBean(source.getFaultBeanEsteso())
        .build();
  }
}
