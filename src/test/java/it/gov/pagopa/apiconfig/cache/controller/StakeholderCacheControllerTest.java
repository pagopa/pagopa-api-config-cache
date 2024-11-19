package it.gov.pagopa.apiconfig.cache.controller;


import it.gov.pagopa.apiconfig.Application;
import it.gov.pagopa.apiconfig.cache.TestUtils;
import it.gov.pagopa.apiconfig.cache.controller.stakeholders.NodeCacheController;
import it.gov.pagopa.apiconfig.cache.model.ConfigData;
import it.gov.pagopa.apiconfig.cache.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.ConfigMapper;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
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
    @Autowired private CacheController cacheController;
    @Autowired private NodeCacheController stakeholderController;

    @Test
    void is404() throws Exception {
        String url = "/stakeholders/node/cache/schemas/v1/idasdasdasd";
        mvc.perform(get(url).contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "/stakeholders/node/cache/schemas/v1",
            "/stakeholders/fdr/cache/schemas/v1",
            "/stakeholders/standin/cache/schemas/v1"
    })
    void testCache(String url) throws Exception {
        String version = "111";
        String cacheVersion = Constants.GZIP_JSON + "-test";
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime romeDateTime = now.withZoneSameInstant(ZoneId.of("Europe/Rome"));

        ConfigDataV1 cd = new ConfigDataV1();
        cd.setVersion("version1");
        ConfigData configData = ConfigData.builder()
                .xCacheId(version)
                .xCacheTimestamp(romeDateTime.toString())
                .xCacheVersion(cacheVersion)
                .configDataV1(cd)
                .build();
        when(stakeholderConfigService.getCache(anyString(), anyString(), any())).thenReturn(configData);

        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string(Constants.HEADER_X_CACHE_ID, version))
                .andExpect(header().string(Constants.HEADER_X_CACHE_VERSION, cacheVersion))
                .andExpect(header().string(Constants.HEADER_X_CACHE_TIMESTAMP, romeDateTime.toString()))
        ;
    }

    @ParameterizedTest
    @CsvSource({
            "/stakeholders/node/cache/schemas/v1/id",
            "/stakeholders/fdr/cache/schemas/v1/id",
            "/stakeholders/standin/cache/schemas/v1/id"
    })
    void testId(String url) throws Exception {
        CacheVersion cacheVersion = CacheVersion.builder()
                .version("111")
                .build();
        when(stakeholderConfigService.getVersionId(anyString(), anyString())).thenReturn(cacheVersion);

        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
        ;
    }

    @ParameterizedTest
    @CsvSource({
            "/stakeholders/node/cache/schemas/v1/xlsx",
            "/stakeholders/fdr/cache/schemas/v1/xlsx",
            "/stakeholders/standin/cache/schemas/v1/xlsx"
    })
    void testXlsx(String url) throws Exception {
        when(stakeholderConfigService.getXLSX(anyString(), anyString())).thenReturn("1".getBytes(StandardCharsets.UTF_8));

        mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }
}
