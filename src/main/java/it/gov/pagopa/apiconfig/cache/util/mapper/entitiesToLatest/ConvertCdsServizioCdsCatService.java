package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsService;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCdsServizioCdsCatService implements Converter<CdsServizio, CdsService> {
  public CdsService convert(MappingContext<CdsServizio, CdsService> mappingContext) {
    CdsServizio source = mappingContext.getSource();
    return CdsService.builder()
        .serviceId(source.getIdServizio())
        .description(source.getDescrizioneServizio())
        .version(source.getVersion())
        .category(source.getCategoria().getDescription())
        .referenceXsd(source.getXsdRiferimento())
        .build();
  }
}
