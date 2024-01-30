package it.gov.pagopa.apiconfig.cache.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

@Component
public class JsonSerializer {

  @Autowired private ObjectMapper objectMapper;

  public byte[] serialize(Map<String,Object> value) throws IOException {
    String stringed = objectMapper.writeValueAsString(value);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
    gzipOut.write(stringed.getBytes(StandardCharsets.UTF_8));
    gzipOut.close();
    return baos.toByteArray();
  }
}
