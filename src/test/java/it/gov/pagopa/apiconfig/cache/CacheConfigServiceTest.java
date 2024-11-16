package it.gov.pagopa.apiconfig.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.service.CacheConfigService;
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
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// @SpringBootTest(classes = Application.class)
@ExtendWith(MockitoExtension.class)
class CacheConfigServiceTest {

  @Mock private RedisRepository redisRepository;

  @InjectMocks private CacheConfigService cacheConfigService;

  @BeforeEach
  void setUp() throws IOException {
    when(redisRepository.get(any())).thenReturn(ZipUtils.zip("{\"version\": \"testversion\"}".getBytes(StandardCharsets.UTF_8)));
  }

  @Test
  void getCacheV1() throws Exception {
    ReflectionTestUtils.setField(cacheConfigService, "keyV1", "k");
    ReflectionTestUtils.setField(cacheConfigService, "objectMapper", new ObjectMapper().findAndRegisterModules());
    Map<String, Object> allData = cacheConfigService.loadFullCache();
    assertThat(allData.size()).isEqualTo(28);
    assertThat(allData.get("version")).isEqualTo("testversion");
  }
}
