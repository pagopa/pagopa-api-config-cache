package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.model.node.v1.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertIntermediariPspToBrokerPspDetails implements
    Converter<IntermediariPsp, BrokerPsp> {

  @Override
  public BrokerPsp convert(MappingContext<IntermediariPsp, BrokerPsp> context) {
    IntermediariPsp source = context.getSource();
    return BrokerPsp.builder()
        .brokerPspCode(source.getIdIntermediarioPsp())
        .enabled(source.getEnabled())
        .description(CommonUtil.deNull(source.getCodiceIntermediario()))
        .extendedFaultBean(source.getFaultBeanEsteso())
        .build();
  }
}
