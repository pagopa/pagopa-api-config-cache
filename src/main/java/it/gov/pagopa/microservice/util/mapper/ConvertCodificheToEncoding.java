package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.Codifiche;
import it.gov.pagopa.microservice.model.creditorinstitution.Encoding;
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
