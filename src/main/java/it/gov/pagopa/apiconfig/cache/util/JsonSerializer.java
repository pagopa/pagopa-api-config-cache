package it.gov.pagopa.apiconfig.cache.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonSerializer {

  @Autowired private ObjectMapper objectMapper;

  public byte[] serialize(ConfigDataV1 value) throws IOException {
    String stringed = objectMapper.writeValueAsString(value);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
    gzipOut.write(stringed.getBytes(StandardCharsets.UTF_8));
    gzipOut.close();
    return baos.toByteArray();
  }

}
