package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.CreditorInstitutionAddress;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertPaToCreditorInstitutionDetails implements Converter<Pa, CreditorInstitution> {

  @Override
  public CreditorInstitution convert(MappingContext<Pa, CreditorInstitution> context) {
    Pa pa = context.getSource();
    return CreditorInstitution.builder()
        .creditorInstitutionCode(pa.getIdDominio())
        .enabled(pa.getEnabled())
        .businessName(pa.getRagioneSociale())
        .address(
            CreditorInstitutionAddress.builder()
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
