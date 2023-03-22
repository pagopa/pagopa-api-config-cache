package it.gov.pagopa.apiconfig;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import it.gov.pagopa.apiconfig.redis.RedisRepository;
import it.gov.pagopa.apiconfig.repository.VerifierRepository;
import it.gov.pagopa.apiconfig.service.VerifierService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class VerifierTest {

  @Mock private RedisRepository redisRepository;
  @Mock private VerifierRepository verifieriRepository;

  @InjectMocks private VerifierService verifierService;

  @BeforeEach
  void setUp() {
    when(verifieriRepository.findAllPaForVerifier()).thenReturn(TestUtils.pastazioniV2);
  }

  @Test
  void getCacheV1() throws Exception {
    List<String> allData = verifierService.getPaV2();
    assertThat(allData.containsAll(TestUtils.pastazioniV2));
  }
}
