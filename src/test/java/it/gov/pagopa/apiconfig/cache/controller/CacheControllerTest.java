package it.gov.pagopa.apiconfig.cache.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.cache.TestUtils;
import it.gov.pagopa.apiconfig.cache.config.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.exception.AppError;
import it.gov.pagopa.apiconfig.cache.exception.AppException;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CacheController.class)
@Import(ConfigMapper.class)
class CacheControllerTest {

  @Autowired private MockMvc mvc;
  @Autowired private ModelMapper modelMapper;

  @MockBean private CacheConfigService cacheConfigService;

  @ParameterizedTest
  @CsvSource({
    "/cache?keys=stations,version",
    "/cache?keys=wrongkey",
    "/cache",
    "/cache/id",
  })
  void testGets(String url) throws Exception {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = DateTimeUtils.getZonedDateTime(now);

    doReturn(TestUtils.inMemoryCache(modelMapper, version, cacheVersion, romeDateTime))
        .when(cacheConfigService)
        .getInMemoryCache();

    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string(Constants.HEADER_X_CACHE_ID, version))
        .andExpect(header().string(Constants.HEADER_X_CACHE_VERSION, cacheVersion))
        .andExpect(
            result -> {
              String timestampHeader =
                  result.getResponse().getHeader(Constants.HEADER_X_CACHE_TIMESTAMP);
              Assertions.assertNotNull(timestampHeader);
              ZonedDateTime actual = ZonedDateTime.parse(timestampHeader);

              // Truncate both to microseconds or milliseconds to normalize
              assertEquals(
                  now.truncatedTo(ChronoUnit.MICROS),
                  actual.truncatedTo(ChronoUnit.MICROS),
                  "X-CACHE-TIMESTAMP does not match");
            });
  }

  @Test
  void keys() throws Exception {
    String url = "/cache/keys";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void inMemoryCacheNotInitialized() throws Exception {
    doThrow(new AppException(AppError.CACHE_NOT_INITIALIZED, "FULL"))
        .when(cacheConfigService)
        .getInMemoryCache();

    String url = "/cache";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().is(404));
  }

  @Test
  void testGetsXlsx() throws Exception {
    String url = "/cache/xlsx";
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    doReturn(
            TestUtils.inMemoryCache(
                modelMapper, version, cacheVersion, DateTimeUtils.getZonedDateTime(now)))
        .when(cacheConfigService)
        .getInMemoryCache();

    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(
            header()
                .string(
                    "Content-Type",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
  }

  @Test
  void refresh_inProgress_false() throws Exception {
    when(cacheConfigService.getCacheInProgress()).thenReturn(false);

    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime cacheTimestamp = DateTimeUtils.getZonedDateTime(now);
    doReturn(TestUtils.inMemoryCache(modelMapper, version, cacheVersion, cacheTimestamp))
        .when(cacheConfigService)
        .getInMemoryCache();

    String url = "/cache/refresh";

    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(header().string(Constants.HEADER_X_CACHE_ID, version))
        .andExpect(header().string(Constants.HEADER_X_CACHE_VERSION, cacheVersion))
        .andExpect(
            header()
                .string(
                    Constants.HEADER_X_CACHE_TIMESTAMP,
                    DateTimeFormatter.ISO_DATE_TIME.format(cacheTimestamp)));

    verify(cacheConfigService, Mockito.times(1)).doCache();
  }

  @Test
  void refresh_inProgress_true() throws Exception {
    when(cacheConfigService.getCacheInProgress()).thenReturn(true);

    String url = "/cache/refresh";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(HttpStatus.SERVICE_UNAVAILABLE.value()));

    verify(cacheConfigService, Mockito.never()).doCache();
    verify(cacheConfigService, Mockito.never()).getInMemoryCache();
  }
}
