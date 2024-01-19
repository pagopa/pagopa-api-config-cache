package it.gov.pagopa.apiconfig.cache.controller;

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
  @Override
  String stakeholder() {
    return "fdr";
  }
  @Override
  String[] keys() {
    return new String[]{
            Constants.version,
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
  }
}
