package it.gov.pagopa.apiconfig.cache.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class ZipUtils {
    public static byte[] unzip(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        GZIPInputStream gzipIn = new GZIPInputStream(bais);
        return gzipIn.readAllBytes();
    }
}
