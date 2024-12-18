package it.gov.pagopa.apiconfig.cache;

import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.repository.VerifierRepository;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class VerifierTest {

  @Mock private RedisRepository redisRepository;
  @Mock private VerifierRepository verifieriRepository;

  @InjectMocks private VerifierService verifierService;

  @Test
  void getCacheV1() throws Exception {
    when(verifieriRepository.findAllPaForVerifier()).thenReturn(TestUtils.pastazioniV2);
    List<String> allData = verifierService.getPaV2();
    assertThat(allData.containsAll(TestUtils.pastazioniV2));
  }

  @Test
  void encoding() throws Exception {
    String s = Constants.ENCODER.encodeToString("listacontroparti>".getBytes(StandardCharsets.UTF_8));
    assertThat(s).isEqualTo("bGlzdGFjb250cm9wYXJ0aT4=");
  }
}
