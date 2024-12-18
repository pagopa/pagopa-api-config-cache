package it.gov.pagopa.apiconfig.cache.controller;

import it.gov.pagopa.apiconfig.Application;
import it.gov.pagopa.apiconfig.cache.TestUtils;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CacheNotInitializedTest {

  @Autowired private MockMvc mvc;

  @MockBean private CacheConfigService cacheConfigService;
  @MockBean private CacheEventHubService cacheEventHubService;
  @MockBean private HealthCheckService healthCheckService;
  @MockBean private VerifierService verifierService;
  @MockBean private EntityManager entityManager;
  @MockBean private StakeholderConfigService stakeholderConfigService;
  @Autowired private ConfigMapper modelMapper;
  @Autowired private CacheController cacheController;

  @BeforeEach
  void setUp() throws IOException {
    String version = "111";
    String cacheVersion = Constants.GZIP_JSON + "-test";
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime romeDateTime = now.withZoneSameInstant(ZoneId.of("Europe/Rome"));

    when(cacheConfigService.getCacheId()).thenReturn(new CacheVersion(version));
    when(cacheConfigService.newCache()).thenReturn(TestUtils.inMemoryCache(modelMapper, version, cacheVersion, romeDateTime));
    when(cacheConfigService.loadFullCache()).thenReturn(TestUtils.inMemoryCache(modelMapper, version, cacheVersion, romeDateTime));
    when(verifierService.getPaV2()).thenReturn(Arrays.asList("1", "2"));
    when(healthCheckService.checkDatabaseConnection()).thenReturn(true);
  }

  @ParameterizedTest
  @CsvSource({
    "/cache/keys"
  })
  void testGetsOK(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @ParameterizedTest
  @CsvSource({
          "/cache?keys=stations,version",
          "/cache?keys=wrongkey",
          "/cache",
  })
  void testGetsKO(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
  }

}
