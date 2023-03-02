package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.model.node.v1.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertConfiguration implements Converter<ConfigurationKeys, ConfigurationKey> {

    @Override
    public ConfigurationKey convert(MappingContext<ConfigurationKeys, ConfigurationKey> context) {
      ConfigurationKeys k = context.getSource();
        return ConfigurationKey.builder()
            .category(k.getConfigCategory())
            .value(k.getConfigValue())
            .description(k.getConfigDescription())
            .key(k.getConfigKey())
                .build();
    }
}
