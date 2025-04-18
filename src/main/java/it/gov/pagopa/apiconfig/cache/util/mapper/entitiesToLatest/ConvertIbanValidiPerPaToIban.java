package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import java.time.ZoneId;
import java.util.Optional;

public class ConvertIbanValidiPerPaToIban implements Converter<IbanValidiPerPa, Iban> {

  @Override
  public Iban convert(MappingContext<IbanValidiPerPa, Iban> context) {
    IbanValidiPerPa source = context.getSource();
    return Iban.builder()
        .ibanValue(source.getIbanAccredito())
        .creditorInstitutionCode(source.getPa().getIdDominio())
        .publicationDate(
            Optional.ofNullable(source.getDataPubblicazione())
                .map(vals -> vals.toInstant().atZone(ZoneId.systemDefault()))
                .orElse(null))
        .validityDate(
            Optional.ofNullable(source.getDataInizioValidita())
                .map(vals -> vals.toInstant().atZone(ZoneId.systemDefault()))
                .orElse(null))
        .idNegozio(source.getIdMerchant())
        .idSellerBank(source.getIdBancaSeller())
        .chiaveAvvio(source.getChiaveAvvio())
        .chiaveEsito(source.getChiaveEsito())
        .build();
  }
}
