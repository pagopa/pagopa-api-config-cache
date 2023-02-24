package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.model.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPaymentTypeToString implements Converter<PaymentType, String> {
    @Override
    public String convert(MappingContext<PaymentType, String> mappingContext) {
        PaymentType paymentType = mappingContext.getSource();
        return paymentType.getPaymentTypeCode();
    }
}
