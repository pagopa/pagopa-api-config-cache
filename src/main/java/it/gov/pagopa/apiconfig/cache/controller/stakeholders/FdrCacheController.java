package it.gov.pagopa.apiconfig.cache.controller.stakeholders;

import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/fdr/cache/schemas")
@Validated
@Slf4j
public class FdrCacheController extends CacheController {

  public static String[] KEYS = new String[]{
          Constants.VERSION,
          Constants.CONFIGURATIONS,
          Constants.CREDITOR_INSTITUTIONS,
          Constants.CREDITOR_INSTITUTION_BROKERS,
          Constants.STATIONS,
          Constants.CREDITOR_INSTITUTION_STATIONS,
          Constants.PSPS,
          Constants.PSP_BROKERS,
          Constants.PAYMENT_TYPES,
          Constants.PSP_CHANNEL_PAYMENT_TYPES,
          Constants.CHANNELS,
          Constants.LANGUAGES,
          Constants.GDE_CONFIGURATIONS
  };

  @Override
  protected String[] keys() {
    return FdrCacheController.KEYS;
  }

}
