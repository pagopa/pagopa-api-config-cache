package it.gov.pagopa.apiconfig.cache.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DefaultFileDeleter implements FileDeleter {
    @Override
    public void delete(Path path) throws IOException {
        Files.delete(path);
    }
}
