package it.gov.pagopa.apiconfig.cache.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.cache.service.HealthCheckService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private HealthCheckService healthCheckService;

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
}
