package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.PaymentType;
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
