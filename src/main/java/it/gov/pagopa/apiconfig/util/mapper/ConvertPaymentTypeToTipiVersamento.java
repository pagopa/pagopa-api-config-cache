package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.PaymentType;
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