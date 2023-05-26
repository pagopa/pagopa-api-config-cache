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

  @Value("${cors.configuration}")
  private String corsConfiguration;

  @SneakyThrows
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    AppCorsConfiguration appCorsConfiguration =
        new ObjectMapper().readValue(corsConfiguration, AppCorsConfiguration.class);
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
