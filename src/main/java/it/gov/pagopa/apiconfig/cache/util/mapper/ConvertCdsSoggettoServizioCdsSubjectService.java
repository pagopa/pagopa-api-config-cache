package it.gov.pagopa.apiconfig.cache.util.mapper;

import it.gov.pagopa.apiconfig.cache.entity.CdsSoggettoServizioCustom;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsSubjectService.CdsSubjectServiceBuilder;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCdsSoggettoServizioCdsSubjectService
    implements Converter<CdsSoggettoServizioCustom, CdsSubjectService> {
  public CdsSubjectService convert(
      MappingContext<CdsSoggettoServizioCustom, CdsSubjectService> mappingContext) {
    CdsSoggettoServizioCustom source = mappingContext.getSource();
    CdsSubjectServiceBuilder build = CdsSubjectService.builder()
        .service(source.getServizio().getIdServizio())
        .subjectServiceId(source.getIdSoggettoServizio())
        .startDate(source.getDataInizioValidita())
        .endDate(source.getDataFineValidita())
        .fee(source.getCommissione())
        .subject(source.getSoggetto().getCreditorInstitutionCode())

        .serviceDescription(source.getDescrizioneServizio());
    if(source.getStazionePa()!=null){
      build.stationCode(source.getStazionePa().getStazione().getIdStazione());
    }
    return build.build();
  }
}
