package it.gov.pagopa.apiconfig.cache.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gov.pagopa.apiconfig.cache.model.node.CacheSchemaVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.util.CacheSchemaVersionDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Centralizes all Jackson {@link ObjectMapper} beans used across the application, so there is a single
 * place that defines serialization behavior (instead of scattered {@code new ObjectMapper()} instances).
 *
 * <p>Two distinct mappers are intentionally kept because the application has two different, coexisting
 * serialization contracts that must NOT be merged (merging them would silently change the cache payload):
 * <ul>
 *   <li>{@link #objectMapper()} - general purpose (HTTP / Spring MVC and the FULL cache). Uses field
 *       visibility {@code ANY}.</li>
 *   <li>{@link #cacheObjectMapper()} - per-stakeholder cache (write / convert / read). Uses DEFAULT
 *       visibility and the schema-aware {@link CacheSchemaVersionDeserializer}.</li>
 * </ul>
 */
@Configuration
public class ObjectMapperConfig {

    /** Bean name of the per-stakeholder cache mapper. */
    public static final String CACHE_OBJECT_MAPPER = "cacheObjectMapper";

    /**
     * Maximum length (in characters) of a single JSON string value allowed when reading, enforced by
     * Jackson's {@link StreamReadConstraints}. Default is 128 MiB: finite (so an unexpectedly huge
     * value fails fast instead of risking an OutOfMemoryError) but well above the current ~20 MB cache.
     */
    @Value("${cache.jackson.max-string-length:134217728}")
    private int maxStringLength;

    /**
     * General-purpose mapper, marked {@link Primary} so it is the one injected by type (Spring MVC,
     * {@code CacheConfigService}, {@code WebMvcConfiguration}). Preserves the previous configuration:
     * all modules auto-registered and field visibility {@code ANY}.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        final var objectMapper = new ObjectMapper(constrainedFactory()).findAndRegisterModules();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }

    /**
     * Mapper dedicated to the per-stakeholder cache. Keeps DEFAULT field visibility (NOT {@code ANY})
     * to preserve the existing contract, registers the {@link JavaTimeModule} and the schema-aware
     * {@link CacheSchemaVersionDeserializer}, and is lenient on unknown properties (the in-memory cache
     * map carries extra keys such as version/timestamp that are not part of {@link ConfigDataV1}).
     */
    @Bean(CACHE_OBJECT_MAPPER)
    public ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper(constrainedFactory());
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(CacheSchemaVersion.class, new CacheSchemaVersionDeserializer(ConfigDataV1.class));
        mapper.registerModule(module);
        return mapper;
    }

    private JsonFactory constrainedFactory() {
        return JsonFactory.builder()
                .streamReadConstraints(StreamReadConstraints.builder()
                        .maxStringLength(maxStringLength)
                        .build())
                .build();
    }
}
