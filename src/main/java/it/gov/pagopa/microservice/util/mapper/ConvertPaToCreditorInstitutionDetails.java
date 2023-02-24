package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.Pa;
import it.gov.pagopa.microservice.model.creditorinstitution.CreditorInstitutionAddress;
import it.gov.pagopa.microservice.model.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.microservice.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertPaToCreditorInstitutionDetails implements Converter<Pa, CreditorInstitution> {

    @Override
    public CreditorInstitution convert(MappingContext<Pa, CreditorInstitution> context) {
        Pa pa = context.getSource();
        return CreditorInstitution.builder()
                .creditorInstitutionCode(pa.getIdDominio())
                .enabled(pa.getEnabled())
                .businessName(CommonUtil.deNull(pa.getRagioneSociale()))
                .address(CreditorInstitutionAddress.builder()
                        .city(pa.getComuneDomicilioFiscale())
                        .location(pa.getIndirizzoDomicilioFiscale())
                        .countryCode(pa.getSiglaProvinciaDomicilioFiscale())
                        .zipCode(pa.getCapDomicilioFiscale())
                        .taxDomicile(pa.getDenominazioneDomicilioFiscale())
                        .build())
                .pspPayment(pa.getPagamentoPressoPsp())
                .reportingFtp(pa.getRendicontazioneFtp())
                .reportingZip(pa.getRendicontazioneZip())
                .build();
    }
}
