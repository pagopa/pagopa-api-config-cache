package it.gov.pagopa.apiconfig.cache.util;

import java.io.IOException;
import java.nio.file.Path;

// wrapper interface needed to be able to test with junit
public interface FileDeleter {
    void delete(Path path) throws IOException;
}
