package it.gov.pagopa.apiconfig.service;

import it.gov.pagopa.apiconfig.redis.RedisRepository;
import it.gov.pagopa.apiconfig.repository.VerifierRepository;
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

  @Value("#{'${canary}'=='true' ? '_canary' : ''}")
  private String keySuffix;

  @Value("apicfg_${spring.database.id}_verifier_v1")
  private String keyV1;

  @Autowired private PlatformTransactionManager transactionManager;
  @Autowired private RedisRepository redisRepository;

  @Autowired private VerifierRepository paRepository;

  public List<String> getPaV2() {
    List<String> allPaForVerifier = paRepository.findAllPaForVerifier();
    redisRepository.pushToRedisAsync(keyV1+keySuffix, allPaForVerifier);
    return allPaForVerifier;
  }
}
