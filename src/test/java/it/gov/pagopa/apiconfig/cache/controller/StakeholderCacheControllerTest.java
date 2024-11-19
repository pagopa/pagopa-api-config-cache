package it.gov.pagopa.apiconfig.cache.controller;


import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StakeholderCacheControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CacheConfigService cacheConfigService;
    @MockBean private StakeholderConfigService stakeholderConfigService;
    @MockBean private CacheEventHubService cacheEventHubService;
    @MockBean private EntityManager entityManager;
    @MockBean private HealthCheckService healthCheckService;
    @MockBean private VerifierService verifierService;

    @Autowired private ConfigMapper modelMapper;

    @Test
    void is404() throws Exception {
        String url = "/stakeholders/node/cache/schemas/v1/idasdasdasd";
        mvc.perform(get(url).contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).andExpect(status().isNotFound());
    }
}
