package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCodifichePaToEncoding
    implements Converter<CodifichePa, CreditorInstitutionEncoding> {

  @Override
  public CreditorInstitutionEncoding convert(
      MappingContext<CodifichePa, CreditorInstitutionEncoding> context) {
    CodifichePa source = context.getSource();
    return CreditorInstitutionEncoding.builder()
        .creditorInstitutionCode(source.getFkPa().getIdDominio())
        .codeType(source.getFkCodifica().getIdCodifica())
        .encodingCode(source.getCodicePa())
        .build();
  }
}
