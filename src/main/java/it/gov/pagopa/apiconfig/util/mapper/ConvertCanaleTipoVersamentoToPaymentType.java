package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCanaleTipoVersamentoToPaymentType implements Converter<CanaleTipoVersamento, PaymentType> {
    @Override
    public PaymentType convert(MappingContext<CanaleTipoVersamento, PaymentType> context) {
        CanaleTipoVersamento source = context.getSource();
        return PaymentType.builder()
                .paymentTypeCode(source.getTipoVersamento().getTipoVersamento())
                .build();
    }
}
