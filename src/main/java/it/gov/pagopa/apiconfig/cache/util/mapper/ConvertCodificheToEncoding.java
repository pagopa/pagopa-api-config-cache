package it.gov.pagopa.apiconfig.cache.util.mapper;

import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.starter.entity.Codifiche;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCodificheToEncoding implements Converter<Codifiche, Encoding> {

  @Override
  public Encoding convert(MappingContext<Codifiche, Encoding> context) {
    Codifiche source = context.getSource();
    return Encoding.builder()
        .codeType(source.getIdCodifica())
        .description(source.getDescrizione())
        .build();
  }
}
