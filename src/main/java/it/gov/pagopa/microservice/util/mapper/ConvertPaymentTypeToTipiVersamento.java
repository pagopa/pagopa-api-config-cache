package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.TipiVersamento;
import it.gov.pagopa.microservice.model.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPaymentTypeToTipiVersamento implements Converter<PaymentType, TipiVersamento> {
    @Override
    public TipiVersamento convert(MappingContext<PaymentType, TipiVersamento> mappingContext) {
        PaymentType paymentType = mappingContext.getSource();
        return TipiVersamento.builder()
                .tipoVersamento(paymentType.getPaymentTypeCode())
                .descrizione(paymentType.getDescription())
                .build();
    }
}
