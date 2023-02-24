package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.CodifichePa;
import it.gov.pagopa.microservice.model.creditorinstitution.CodeTypeEnum;
import it.gov.pagopa.microservice.model.creditorinstitution.CreditorInstitutionEncoding;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertCodifichePaToEncoding implements Converter<CodifichePa, CreditorInstitutionEncoding> {

    @Override
    public CreditorInstitutionEncoding convert(MappingContext<CodifichePa, CreditorInstitutionEncoding> context) {
        CodifichePa source = context.getSource();
        return CreditorInstitutionEncoding.builder()
            .creditorInstitutionCode(source.getCodicePa())
                .codeType(getCodeType(source).getValue())
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
