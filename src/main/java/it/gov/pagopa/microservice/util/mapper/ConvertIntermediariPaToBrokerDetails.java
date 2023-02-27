package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.IntermediariPa;
import it.gov.pagopa.microservice.model.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.microservice.util.CommonUtil;
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
