package it.gov.pagopa.apiconfig.cache.util.mapper;

import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.PaymentType;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPaymentTypeToString implements Converter<PaymentType, String> {

  @Override
  public String convert(MappingContext<PaymentType, String> mappingContext) {
    PaymentType paymentType = mappingContext.getSource();
    return paymentType.getPaymentTypeCode();
  }
}
