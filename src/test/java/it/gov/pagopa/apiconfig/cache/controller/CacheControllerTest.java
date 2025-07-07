package it.gov.pagopa.apiconfig.cache.controller;

import it.gov.pagopa.apiconfig.Application;
import it.gov.pagopa.apiconfig.cache.TestUtils;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import it.gov.pagopa.apiconfig.cache.util.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class CacheControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private CacheConfigService cacheConfigService;
  @MockBean private StakeholderConfigService stakeholderConfigService;
  @MockBean private CacheEventHubService cacheEventHubService;
  @MockBean private EntityManager entityManager;
  @MockBean private HealthCheckService healthCheckService;
  @MockBean private VerifierService verifierService;

  @Autowired private ConfigMapper modelMapper;
  @Autowired private CacheController cacheController;

  @Test
  void home() throws Exception {
    String url = "/";
    mvc.perform(get(url)).andExpect(status().is3xxRedirection());
  }

  @Test
  void info() throws Exception {
    String url = "/info";
    mvc.perform(get(url)).andExpect(status().isOk());
  }

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

    TestUtils.inizializeInMemoryCache(cacheController, modelMapper, version, cacheVersion, romeDateTime);
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string(Constants.HEADER_X_CACHE_ID, version))
            .andExpect(header().string(Constants.HEADER_X_CACHE_VERSION, cacheVersion))
            .andExpect(result -> {
              String timestampHeader = result.getResponse().getHeader(Constants.HEADER_X_CACHE_TIMESTAMP);
                Assertions.assertNotNull(timestampHeader);
                ZonedDateTime actual = ZonedDateTime.parse(timestampHeader);

              // Truncate both to microseconds or milliseconds to normalize
              assertEquals(
                      now.truncatedTo(ChronoUnit.MICROS),
                      actual.truncatedTo(ChronoUnit.MICROS),
                      "X-CACHE-TIMESTAMP does not match"
              );
            });
  }

  @Test
  void keys() throws Exception {
    String url = "/cache/keys";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
  }

  @Test
  void inMemoryCacheNotInitialized() throws Exception {
    org.springframework.test.util.ReflectionTestUtils.setField(cacheController, "inMemoryCache", null);
    String url = "/cache";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(404));
  }

  @Test
  void testGetsXlsx() throws Exception {
    String url = "/cache/xlsx";
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    TestUtils.inizializeInMemoryCache(cacheController, modelMapper, version, cacheVersion, DateTimeUtils.getZonedDateTime(now));
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
  }

  @Test
  void refresh_inProgress_false() throws Exception {
    when(cacheConfigService.getCacheInProgress()).thenReturn(false);

    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    when(cacheConfigService.newCache()).thenReturn(TestUtils.inMemoryCache(modelMapper, version, cacheVersion, DateTimeUtils.getZonedDateTime(now)));

    String url = "/cache/refresh";

    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(header().string(Constants.HEADER_X_CACHE_ID, version))
            .andExpect(header().string(Constants.HEADER_X_CACHE_VERSION, cacheVersion))
            .andExpect(header().string(Constants.HEADER_X_CACHE_TIMESTAMP, DateTimeUtils.getString(now)))
    ;
  }

  @Test
  void refresh_inProgress_true() throws Exception {
    when(cacheConfigService.getCacheInProgress()).thenReturn(true);

    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    when(cacheConfigService.newCache()).thenReturn(TestUtils.inMemoryCache(modelMapper, version, cacheVersion, DateTimeUtils.getZonedDateTime(now)));

    String url = "/cache/refresh";

    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is(HttpStatus.SERVICE_UNAVAILABLE.value()))
    ;
  }

  // TODO refresh


}
