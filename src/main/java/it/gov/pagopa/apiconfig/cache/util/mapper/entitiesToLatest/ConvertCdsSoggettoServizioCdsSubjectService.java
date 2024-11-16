package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsSubjectService.CdsSubjectServiceBuilder;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggettoServizio;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCdsSoggettoServizioCdsSubjectService
    implements Converter<CdsSoggettoServizio, CdsSubjectService> {
  public CdsSubjectService convert(
      MappingContext<CdsSoggettoServizio, CdsSubjectService> mappingContext) {
    CdsSoggettoServizio source = mappingContext.getSource();
    CdsSubjectServiceBuilder build =
        CdsSubjectService.builder()
            .service(source.getServizio().getIdServizio())
            .subjectServiceId(source.getIdSoggettoServizio())
            .startDate(source.getDataInizioValidita())
            .endDate(source.getDataFineValidita())
            .fee(source.getCommissione())
            .subject(source.getSoggetto().getCreditorInstitutionCode())
            .serviceDescription(source.getDescrizioneServizio());
    if (source.getStazionePa() != null) {
      build.stationCode(source.getStazionePa().getFkStazione().getIdStazione());
    }
    return build.build();
  }
}
