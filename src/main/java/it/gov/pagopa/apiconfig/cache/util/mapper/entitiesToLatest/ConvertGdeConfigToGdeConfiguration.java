package it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest;

import it.gov.pagopa.apiconfig.cache.model.latest.configuration.GdeConfiguration;
import it.gov.pagopa.apiconfig.starter.entity.GdeConfig;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

/**
 * Converter class that specify how to convert a {@link GdeConfig} instance to a {@link GdeConfiguration} instance
 */
public class ConvertGdeConfigToGdeConfiguration implements Converter<GdeConfig, GdeConfiguration> {

  @Override
  public GdeConfiguration convert(MappingContext<GdeConfig, GdeConfiguration> context) {
    GdeConfig source = context.getSource();

    return GdeConfiguration.builder()
            .primitiva(source.getPrimitiva().toUpperCase()) // force upper case to mimic Nodo behaviour
            .type(source.getType().toUpperCase()) // force upper case to mimic Nodo behaviour
            .eventHubEnabled(source.getEventHubEnabled())
            .eventHubPayloadEnabled(source.getEventHubPayloadEnabled())
            .build();
  }
}