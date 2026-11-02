package it.gov.pagopa.apiconfig.cache.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.model.node.CacheSchemaVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import java.io.IOException;

public class CacheSchemaVersionDeserializer extends JsonDeserializer<CacheSchemaVersion> {

  private final Class<?> targetType;

  public CacheSchemaVersionDeserializer() {
    this.targetType = null;
  }

  public CacheSchemaVersionDeserializer(Class<?> targetType) {
    this.targetType = targetType;
  }

  @Override
  public CacheSchemaVersion deserialize(
      JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

    if (this.targetType != null && this.targetType.isAssignableFrom(ConfigDataV1.class)) {
      return jsonParser.readValueAs(ConfigDataV1.class);
    } else {
      throw new AppException(AppError.CACHE_SCHEMA_NOT_VALID);
    }
  }
}
