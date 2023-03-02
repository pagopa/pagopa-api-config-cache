package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertIntermediariPaToBrokerDetails implements Converter<IntermediariPa, BrokerCreditorInstitution> {

    @Override
    public BrokerCreditorInstitution convert(MappingContext<IntermediariPa, BrokerCreditorInstitution> context) {
        IntermediariPa source = context.getSource();
        return BrokerCreditorInstitution.builder()
                .enabled(source.getEnabled())
                .brokerCode(source.getIdIntermediarioPa())
                .description(CommonUtil.deNull(source.getCodiceIntermediario()))
                .extendedFaultBean(source.getFaultBeanEsteso())
                .build();
    }
}
