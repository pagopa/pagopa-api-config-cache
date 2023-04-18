package it.gov.pagopa.apiconfig.cache.util.mapper;

import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.CodeTypeEnum;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCodifichePaToEncoding
    implements Converter<CodifichePa, CreditorInstitutionEncoding> {

  @Override
  public CreditorInstitutionEncoding convert(
      MappingContext<CodifichePa, CreditorInstitutionEncoding> context) {
    CodifichePa source = context.getSource();
    CodeTypeEnum codeType = getCodeType(source);
    return CreditorInstitutionEncoding.builder()
        .creditorInstitutionCode(source.getFkPa().getIdDominio())
        .codeType(codeType != null ? codeType : null)
        .encodingCode(source.getCodicePa())
        .build();
  }

  /**
   * Null-safe conversion
   *
   * @param source {@link CodifichePa}
   * @return {@link CreditorInstitutionEncoding.CodeTypeEnum}
   */
  private CodeTypeEnum getCodeType(CodifichePa source) {
    if (source != null && source.getFkCodifica() != null) {
      return CodeTypeEnum.fromValue(source.getFkCodifica().getIdCodifica());
    } else {
      return null;
    }
  }
}
