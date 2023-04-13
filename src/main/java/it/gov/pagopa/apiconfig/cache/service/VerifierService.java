package it.gov.pagopa.apiconfig.cache.service;

import it.gov.pagopa.apiconfig.cache.redis.RedisRepository;
import it.gov.pagopa.apiconfig.cache.repository.VerifierRepository;
import java.util.List;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Service
@Transactional
public class VerifierService {

  @Value("apicfg_${spring.database.id}_verifier_v1")
  private String keyV1;

  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private RedisRepository redisRepository;

  @Autowired private VerifierRepository paRepository;

  public List<String> getPaV2() {
    List<String> allPaForVerifier = paRepository.findAllPaForVerifier();
    redisRepository.pushToRedisAsync(keyV1, allPaForVerifier);
    return allPaForVerifier;
  }
}
