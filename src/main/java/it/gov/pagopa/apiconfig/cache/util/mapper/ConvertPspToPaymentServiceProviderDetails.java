package it.gov.pagopa.apiconfig.cache.util.mapper;

import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPspToPaymentServiceProviderDetails
    implements Converter<Psp, PaymentServiceProvider> {

  @Override
  public PaymentServiceProvider convert(MappingContext<Psp, PaymentServiceProvider> context) {
    Psp source = context.getSource();
    return PaymentServiceProvider.builder()
        .pspCode(source.getIdPsp())
        .enabled(source.getEnabled())
        .description(source.getDescrizione())
        .businessName(source.getRagioneSociale())
        .abi(source.getAbi())
        .bic(source.getBic())
        .myBankCode(source.getCodiceMybank())
        .digitalStamp(source.getMarcaBolloDigitale())
        .agidPsp(source.getAgidPsp())
        .taxCode(source.getCodiceFiscale())
        .vatNumber(source.getVatNumber())
        .build();
  }
}
