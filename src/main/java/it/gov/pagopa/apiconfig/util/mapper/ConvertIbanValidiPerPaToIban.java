package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.Iban;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertIbanValidiPerPaToIban implements Converter<IbanValidiPerPa, Iban> {

  @Override
  public Iban convert(MappingContext<IbanValidiPerPa, Iban> context) {
    IbanValidiPerPa source = context.getSource();
    return Iban.builder()
        .ibanValue(source.getIbanAccredito())
        .creditorInstitutionCode(source.getPa().getIdDominio())
        .publicationDate(source.getDataPubblicazione())
        .validityDate(source.getDataInizioValidita())
        .validityDate(source.getDataInizioValidita())
        .idNegozio(source.getIdMerchant())
        .idSellerBank(source.getIdBancaSeller())
        .chiaveAvvio(source.getChiaveAvvio())
        .chiaveEsito(source.getChiaveEsito())
        .build();
  }
}
