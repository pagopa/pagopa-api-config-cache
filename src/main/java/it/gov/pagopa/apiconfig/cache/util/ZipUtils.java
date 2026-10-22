package it.gov.pagopa.apiconfig.cache.util;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class ZipUtils {

    // Safety ceiling for decompressed payloads to avoid unbounded heap allocation (zip-bomb guard)
    private static final long MAX_DECOMPRESSED_BYTES = 1L << 30; // 1 GiB
    private static final int BUFFER_SIZE = 8192;

    private ZipUtils(){}

    /**
     * Returns a decompressing stream over the given gzip payload, so callers (e.g. Jackson) can parse
     * the content incrementally without materializing the full uncompressed byte[] in memory.
     * The stream enforces {@link #MAX_DECOMPRESSED_BYTES} as a zip-bomb / OOM guard.
     */
    public static InputStream unzipToStream(byte[] bytes) throws IOException {
        if (bytes == null) {
            return new ByteArrayInputStream(new byte[0]);
        }
        GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(bytes), BUFFER_SIZE);
        return new BoundedInputStream(gzipIn, MAX_DECOMPRESSED_BYTES);
    }

    private static final class BoundedInputStream extends FilterInputStream {
        private final long maxBytes;
        private long count;

        BoundedInputStream(InputStream in, long maxBytes) {
            super(in);
            this.maxBytes = maxBytes;
        }

        @Override
        public int read() throws IOException {
            int b = super.read();
            if (b != -1) {
                increment(1);
            }
            return b;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int n = super.read(b, off, len);
            if (n > 0) {
                increment(n);
            }
            return n;
        }

        private void increment(long n) throws IOException {
            count += n;
            if (count > maxBytes) {
                throw new IOException(
                    "Decompressed payload exceeds maximum allowed size of " + maxBytes + " bytes");
            }
        }
    }
}
