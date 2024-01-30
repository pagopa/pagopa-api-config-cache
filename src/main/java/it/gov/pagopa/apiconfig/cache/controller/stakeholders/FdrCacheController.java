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
          Constants.version,
          Constants.configurations,
          Constants.creditorInstitutions,
          Constants.creditorInstitutionBrokers,
          Constants.stations,
          Constants.creditorInstitutionStations,
          Constants.psps,
          Constants.pspBrokers,
          Constants.paymentTypes,
          Constants.pspChannelPaymentTypes,
          Constants.channels,
          Constants.languages,
          Constants.gdeConfigurations
  };

  @Override
  protected String[] keys() {
    return FdrCacheController.KEYS;
  }

}
