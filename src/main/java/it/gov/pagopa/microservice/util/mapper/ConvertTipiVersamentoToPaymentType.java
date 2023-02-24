package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.TipiVersamento;
import it.gov.pagopa.microservice.model.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertTipiVersamentoToPaymentType implements Converter<TipiVersamento, PaymentType> {
    @Override
    public PaymentType convert(MappingContext<TipiVersamento, PaymentType> mappingContext) {
        TipiVersamento tipiVersamento = mappingContext.getSource();
        return PaymentType.builder()
                .description(tipiVersamento.getDescrizione())
                .paymentTypeCode(tipiVersamento.getTipoVersamento())
                .build();
    }
}
