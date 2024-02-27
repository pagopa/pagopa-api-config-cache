package it.gov.pagopa.apiconfig.cache.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.Application;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import javax.persistence.EntityManager;

import it.gov.pagopa.apiconfig.cache.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class ControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private ConfigService configService;
  @MockBean private CacheEventHubService cacheEventHubService;
  @MockBean private HealthCheckService healthCheckService;
  @MockBean private VerifierService verifierService;
  @MockBean private EntityManager entityManager;

  @BeforeEach
  void setUp() throws IOException {
    HashMap<String, Object> objectObjectHashMap = new HashMap<String, Object>();
    objectObjectHashMap.put(Constants.version,"1111");
    objectObjectHashMap.put(Constants.cacheVersion,"1111");
    objectObjectHashMap.put(Constants.timestamp, ZonedDateTime.now());
    when(configService.getCacheV1Id("")).thenReturn(new CacheVersion("1111"));
    when(configService.newCacheV1()).thenReturn(objectObjectHashMap);
    when(verifierService.getPaV2()).thenReturn(Arrays.asList("1", "2"));
    when(healthCheckService.checkDatabaseConnection()).thenReturn(true);
  }

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
    "/stakeholders/node/cache/schemas/v1",
    "/stakeholders/node/cache/schemas/v1/id",
    "/stakeholders/fdr/cache/schemas/v1",
    "/stakeholders/fdr/cache/schemas/v1/id",
    "/stakeholders/verifier/cache/schemas/v1"
  })
  void testGets(String url) throws Exception {
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void is404() throws Exception {
    String url = "/stakeholders/node/cache/schemas/v1/idasdasdasd";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
  }
}
