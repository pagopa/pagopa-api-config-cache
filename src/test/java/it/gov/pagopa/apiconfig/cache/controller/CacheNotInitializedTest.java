package it.gov.pagopa.apiconfig.cache.controller;

import it.gov.pagopa.apiconfig.Application;
import it.gov.pagopa.apiconfig.cache.TestUtils;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.Channel;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CacheNotInitializedTest {

  @Autowired private MockMvc mvc;

  @MockBean private ConfigService configService;
  @MockBean private CacheEventHubService cacheEventHubService;
  @MockBean private HealthCheckService healthCheckService;
  @MockBean private VerifierService verifierService;
  @MockBean private EntityManager entityManager;
  @Autowired private ConfigMapper modelMapper;
  @Autowired private CacheController cacheController;

  @BeforeEach
  void setUp() throws IOException {
    HashMap<String, Object> objectObjectHashMap = new HashMap<String, Object>();
    objectObjectHashMap.put(Constants.VERSION,"1111");
    objectObjectHashMap.put(Constants.CACHE_VERSION,"1111");
    objectObjectHashMap.put(Constants.TIMESTAMP, ZonedDateTime.now());

    List<Station> stations = modelMapper.modelMapper().map(
            TestUtils.stazioni,
            new TypeToken<List<Station>>(){}.getType());

    objectObjectHashMap.putAll(stations.stream().collect(Collectors.toMap(
            ss->((Station)ss).getStationCode(),
            ss->ss
            )));

    List<Channel> channels = modelMapper.modelMapper().map(
            TestUtils.canali,
            new TypeToken<List<Channel>>(){}.getType());

    objectObjectHashMap.putAll(channels.stream().collect(Collectors.toMap(
            ss->((Channel)ss).getChannelCode(),
            ss->ss
    )));

    when(configService.getCacheV1Id("")).thenReturn(new CacheVersion("1111"));
    when(configService.newCacheV1()).thenReturn(objectObjectHashMap);
    when(configService.loadFullCache()).thenReturn(objectObjectHashMap);
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
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
  }

}
