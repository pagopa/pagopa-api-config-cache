package it.gov.pagopa.apiconfig.cache.controller.stakeholders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VerifierCacheController.class)
class VerifierCacheControllerTest {

  @Autowired private MockMvc mvc;

  @MockBean private VerifierService verifierService;

  @Test
  void cache_returnsList() throws Exception {
    List<String> payload = List.of("pa-1", "pa-2");
    when(verifierService.getPaV2()).thenReturn(payload);

    mvc.perform(get("/stakeholders/verifier/cache/schemas/v1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
        .andExpect(jsonPath("$[0]").value("pa-1"))
        .andExpect(jsonPath("$[1]").value("pa-2"));

    verify(verifierService, Mockito.times(1)).getPaV2();
  }

  @Test
  void cache_returnsEmptyList() throws Exception {
    when(verifierService.getPaV2()).thenReturn(Collections.emptyList());

    mvc.perform(get("/stakeholders/verifier/cache/schemas/v1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(0)));

    verify(verifierService, Mockito.times(1)).getPaV2();
  }

  @Test
  void cache_whenServiceThrows_returns500() throws Exception {
    when(verifierService.getPaV2()).thenThrow(new RuntimeException("boom"));

    mvc.perform(get("/stakeholders/verifier/cache/schemas/v1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().is5xxServerError());

    verify(verifierService, Mockito.times(1)).getPaV2();
  }
}
