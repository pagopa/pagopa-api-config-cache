package it.gov.pagopa.apiconfig.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.controller.stakeholders.FdrCacheController;
import it.gov.pagopa.apiconfig.cache.controller.HomeController;
import it.gov.pagopa.apiconfig.cache.controller.stakeholders.NodeCacheController;
import it.gov.pagopa.apiconfig.cache.controller.stakeholders.VerifierCacheController;
import it.gov.pagopa.apiconfig.cache.service.CacheEventHubService;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import it.gov.pagopa.apiconfig.cache.service.StakeholderConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.JsonSerializer;
import it.gov.pagopa.apiconfig.starter.repository.HealthCheckRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiGenerationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    FdrCacheController fdrCacheController;

    @MockBean
    NodeCacheController nodeCacheController;

    @MockBean
    CacheConfigService cacheConfigService;

    @MockBean
    HomeController homeController;

    @MockBean
    VerifierCacheController verifierCacheController;

    @MockBean
    HealthCheckService healthCheckService;

    @MockBean
    StakeholderConfigService stakeholderConfigService;

    @MockBean
    VerifierService verifierService;

    @MockBean
    HealthCheckRepository healthCheckRepository;

    @MockBean
    JsonSerializer jsonSerializer;

    @MockBean
    CacheEventHubService cacheEventHubService;

    @Test
    void swaggerSpringPlugin() throws Exception {
        getSwagger("/v3/api-docs", "openapi.json");
        getSwagger("/v3/api-docs/nodev1", "openapi_nodev1.json");
        getSwagger("/v3/api-docs/verifierv1", "openapi_verifierv1.json");
        getSwagger("/v3/api-docs/fdrv1", "openapi_fdrv1.json");
        getSwagger("/v3/api-docs/standinV1", "openapi_standinv1.json");
        getSwagger("/v3/api-docs/export", "openapi_export.json");
    }

    private void getSwagger(String urlTemplate, String fileName) throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(urlTemplate).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andDo(
                        (result) -> {
                            assertNotNull(result);
                            assertNotNull(result.getResponse());
                            final String content = result.getResponse().getContentAsString();
                            assertFalse(content.isBlank());
//                            assertFalse(content.contains("${"), "Generated swagger contains placeholders");
                            Object swagger =
                                    objectMapper.readValue(result.getResponse().getContentAsString(), Object.class);
                            String formatted =
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(swagger);
                            Path basePath = Paths.get("openapi/");
                            Files.createDirectories(basePath);
                            Files.write(basePath.resolve(fileName), formatted.getBytes());
                        });
    }
}
