package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.IntermediariPa;
import it.gov.pagopa.microservice.model.creditorinstitution.BrokerDetails;
import it.gov.pagopa.microservice.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertIntermediariPaToBrokerDetails implements Converter<IntermediariPa, BrokerDetails> {

    @Override
    public BrokerDetails convert(MappingContext<IntermediariPa, BrokerDetails> context) {
        IntermediariPa source = context.getSource();
        return BrokerDetails.builder()
                .enabled(source.getEnabled())
                .brokerCode(source.getIdIntermediarioPa())
                .description(CommonUtil.deNull(source.getCodiceIntermediario()))
                .extendedFaultBean(source.getFaultBeanEsteso())
                .build();
    }
}
