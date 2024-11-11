package it.gov.pagopa.apiconfig.cache.util.mapper;

import it.gov.pagopa.apiconfig.cache.model.latest.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertIntermediariPspToBrokerPspDetails
    implements Converter<IntermediariPsp, BrokerPsp> {

  @Override
  public BrokerPsp convert(MappingContext<IntermediariPsp, BrokerPsp> context) {
    IntermediariPsp source = context.getSource();
    return BrokerPsp.builder()
        .brokerPspCode(source.getIdIntermediarioPsp())
        .enabled(source.getEnabled())
        .description(source.getCodiceIntermediario())
        .extendedFaultBean(source.getFaultBeanEsteso())
        .build();
  }
}
