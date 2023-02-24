package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.Psp;
import it.gov.pagopa.microservice.model.psp.PaymentServiceProvider;
import it.gov.pagopa.microservice.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPspToPaymentServiceProviderDetails implements Converter<Psp, PaymentServiceProvider> {

    @Override
    public PaymentServiceProvider convert(MappingContext<Psp, PaymentServiceProvider> context) {
        Psp source = context.getSource();
        return PaymentServiceProvider.builder()
                .pspCode(source.getIdPsp())
                .enabled(source.getEnabled())
                .description(source.getDescrizione())
                .businessName(CommonUtil.deNull(source.getRagioneSociale()))
                .abi(source.getAbi())
                .bic(source.getBic())
                .myBankCode(source.getCodiceMybank())
                .stamp(source.getMarcaBolloDigitale())
                .agidPsp(source.getAgidPsp())
                .taxCode(source.getCodiceFiscale())
                .vatNumber(source.getVatNumber())
                .build();
    }
}
