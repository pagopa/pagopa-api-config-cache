package it.gov.pagopa.apiconfig.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.Application;
import it.gov.pagopa.apiconfig.model.node.CacheVersion;
import it.gov.pagopa.apiconfig.model.node.v1.ConfigDataV1;
import it.gov.pagopa.apiconfig.service.ConfigService;
import it.gov.pagopa.apiconfig.service.VerifierService;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
  @MockBean private VerifierService verifierService;

  @BeforeEach
  void setUp() throws IOException {
    when(configService.getCacheV1Id()).thenReturn(new CacheVersion("1111"));
    when(configService.newCacheV1()).thenReturn(new ConfigDataV1());
    when(verifierService.getPaV2()).thenReturn(Arrays.asList("1", "2"));
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

  @Test
  void getV1Cache() throws Exception {
    String url = "/stakeholders/node/cache/schemas/v1";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void getV1() throws Exception {
    String url = "/stakeholders/node/cache/schemas/v1/id";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void verifier() throws Exception {
    String url = "/stakeholders/verifier/cache";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  void is404() throws Exception {
    String url = "/stakeholders/node/cache/schemas/v1/idasdasdasd";
    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
  }
  //
  //  @Test
  //  void getBroker() throws Exception {
  //    String url = "/brokers/1234";
  //    mvc.perform(get(url).contentType(MediaType.APPLICATION_JSON))
  //        .andExpect(status().isOk())
  //        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  //  }
  //
  //  @Test
  //  void createBroker() throws Exception {
  //    mvc.perform(
  //            post("/brokers")
  //                .content(TestUtil.toJson(getMockBrokerDetails()))
  //                .contentType(MediaType.APPLICATION_JSON))
  //        .andExpect(status().isCreated())
  //        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  //  }
  //
  //  @Test
  //  void createBroker_400() throws Exception {
  //    mvc.perform(
  //            post("/brokers")
  //
  // .content(TestUtil.toJson(getMockBrokerDetails().toBuilder().brokerCode("").build()))
  //                .contentType(MediaType.APPLICATION_JSON))
  //        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
  //        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  //  }
  //
  //  @Test
  //  void updateBroker() throws Exception {
  //    mvc.perform(
  //            put("/brokers/1234")
  //                .content(TestUtil.toJson(getMockBrokerDetails()))
  //                .contentType(MediaType.APPLICATION_JSON))
  //        .andExpect(status().isOk())
  //        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  //  }
  //
  //  @Test
  //  void updateBroker_400() throws Exception {
  //    mvc.perform(
  //            put("/brokers/1234")
  //
  // .content(TestUtil.toJson(getMockBrokerDetails().toBuilder().brokerCode("").build()))
  //                .contentType(MediaType.APPLICATION_JSON))
  //        .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
  //        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  //  }
  //
  //  @Test
  //  void deleteBroker() throws Exception {
  //    mvc.perform(delete("/brokers/1234").contentType(MediaType.APPLICATION_JSON))
  //        .andExpect(status().isOk());
  //  }
}
