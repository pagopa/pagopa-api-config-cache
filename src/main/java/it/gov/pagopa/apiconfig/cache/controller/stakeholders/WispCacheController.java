package it.gov.pagopa.apiconfig.cache.controller.stakeholders;

import it.gov.pagopa.apiconfig.cache.controller.StakeholderController;
import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/wisp/cache/schemas")
@Validated
@Slf4j
public class WispCacheController extends StakeholderController {
  public static String[] KEYS = new String[]{
          Constants.VERSION,
          Constants.CREDITOR_INSTITUTIONS,
          Constants.CREDITOR_INSTITUTION_BROKERS,
          Constants.STATIONS,
          Constants.CREDITOR_INSTITUTION_STATIONS,
          Constants.IBANS,
          Constants.PSPS,
          Constants.PSP_BROKERS,
          Constants.PAYMENT_TYPES,
          Constants.PSP_CHANNEL_PAYMENT_TYPES,
          Constants.CHANNELS,
          Constants.PLUGINS,
          Constants.CONFIGURATIONS
  };

  public static Stakeholder STAKEHOLDER = Stakeholder.WISP;

  @Override
  protected String[] keys() {
    return WispCacheController.KEYS;
  }

  @Override
  protected Stakeholder stakeholder() {
    return WispCacheController.STAKEHOLDER;
  }

}
