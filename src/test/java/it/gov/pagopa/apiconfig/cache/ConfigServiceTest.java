package it.gov.pagopa.apiconfig.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.repository.VerifierRepository;
import it.gov.pagopa.apiconfig.cache.service.ConfigService;
import it.gov.pagopa.apiconfig.cache.service.VerifierService;
import it.gov.pagopa.apiconfig.cache.util.ZipUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class ConfigServiceTest {

  @Mock private RedisRepository redisRepository;

  @InjectMocks private ConfigService configService;

  @BeforeEach
  void setUp() throws IOException {
    when(redisRepository.get(any())).thenReturn(ZipUtils.zip("{\"version\": \"testversion\"}".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void getCacheV1() throws Exception {
    ReflectionTestUtils.setField(configService, "keyV1", "k");
    ReflectionTestUtils.setField(configService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    Map<String, Object> allData = configService.loadFullCache();
    assertThat(allData.size()).isEqualTo(28);
    assertThat(allData.get("version")).isEqualTo("testversion");
  }
}
