package it.gov.pagopa.apiconfig.cache.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

@Component
public class JsonSerializer {

  @Autowired private ObjectMapper objectMapper;

  public byte[] serialize(Map<String,Object> value) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (GZIPOutputStream gzipOut = new GZIPOutputStream(baos)) {
      objectMapper.writeValue(gzipOut, value);
    }
    return baos.toByteArray();
  }
}
