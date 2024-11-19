package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.Application;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class, properties = {
        "preload=true"
})
@AutoConfigureMockMvc
class RefreshTest {

  @Autowired private MockMvc mvc;

  @MockBean private CacheConfigService cacheConfigService;
  @MockBean private StakeholderConfigService stakeholderConfigService;
  @MockBean private CacheEventHubService cacheEventHubService;
  @MockBean private HealthCheckService healthCheckService;
  @MockBean private VerifierService verifierService;
  @MockBean private EntityManager entityManager;

  @ParameterizedTest
  @CsvSource({
          "/cache/id",
  })
  void testGets(String url) throws Exception {
    //torna errore perchè il postconstruct gira prima del mocker,quindi la cache è vuota
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is5xxServerError());
  }
}
