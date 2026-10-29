package it.gov.pagopa.apiconfig.cache.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.model.AppCorsConfiguration;
import it.gov.pagopa.apiconfig.cache.model.StringToNodeCacheKeyConverter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
public class WebMvcConfiguration implements WebMvcConfigurer {

  private final String corsConfiguration;
  private final ObjectMapper objectMapper;

  public WebMvcConfiguration(
      @Value("${cors.configuration}") String corsConfiguration, ObjectMapper objectMapper) {
    this.corsConfiguration = corsConfiguration;
    this.objectMapper = objectMapper;
  }

  @SneakyThrows
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    AppCorsConfiguration appCorsConfiguration =
        objectMapper.readValue(corsConfiguration, AppCorsConfiguration.class);
    registry
        .addMapping("/**")
        .allowedOrigins(appCorsConfiguration.getOrigins())
        .allowedMethods(appCorsConfiguration.getMethods());
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToNodeCacheKeyConverter());
  }
}
