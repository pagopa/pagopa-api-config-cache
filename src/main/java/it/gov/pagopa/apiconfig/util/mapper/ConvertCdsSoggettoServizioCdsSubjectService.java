package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.model.node.v1.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggettoServizio;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCdsSoggettoServizioCdsSubjectService implements Converter<CdsSoggettoServizio, CdsSubjectService>  {
  public CdsSubjectService convert(
      MappingContext<CdsSoggettoServizio, CdsSubjectService> mappingContext) {
    CdsSoggettoServizio source = mappingContext.getSource();
    return CdsSubjectService.builder()
        .service(source.getServizio().getIdServizio())
        .subjectServiceId(source.getIdSoggettoServizio())
        .startDate(source.getDataInizioValidita())
        .endDate(source.getDataFineValidita())
        .fee(source.getCommissione())
        .subject(source.getSoggetto().getCreditorInstitutionCode())
        .build();
  }
}
