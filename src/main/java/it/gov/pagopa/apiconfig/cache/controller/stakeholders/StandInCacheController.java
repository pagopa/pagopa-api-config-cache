package it.gov.pagopa.apiconfig.cache.controller.stakeholders;

import it.gov.pagopa.apiconfig.cache.controller.StakeholderController;
import it.gov.pagopa.apiconfig.cache.model.Stakeholder;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import oracle.jdbc.driver.Const;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/standin/cache/schemas")
@Validated
@Slf4j
public class StandInCacheController extends StakeholderController {

  public static String[] KEYS = new String[]{
          Constants.VERSION,
          Constants.STATIONS,
          Constants.CREDITOR_INSTITUTION_STATIONS
  };

  public static Stakeholder STAKEHOLDER = Stakeholder.STANDIN;

  @Override
  protected String[] keys() {
    return StandInCacheController.KEYS;
  }

  @Override
  protected Stakeholder stakeholder() {
    return StandInCacheController.STAKEHOLDER;
  }

}
