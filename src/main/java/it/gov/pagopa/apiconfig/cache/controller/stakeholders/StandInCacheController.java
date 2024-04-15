package it.gov.pagopa.apiconfig.cache.controller.stakeholders;

import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/standin/cache/schemas")
@Validated
@Slf4j
public class StandInCacheController extends CacheController {

  public static String[] KEYS = new String[]{
          Constants.VERSION,
          Constants.STATIONS,
  };

  @Override
  protected String[] keys() {
    return StandInCacheController.KEYS;
  }

}
