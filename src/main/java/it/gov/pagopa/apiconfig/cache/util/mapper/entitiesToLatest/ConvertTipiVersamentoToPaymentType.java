package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.configuration.PaymentType;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
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
