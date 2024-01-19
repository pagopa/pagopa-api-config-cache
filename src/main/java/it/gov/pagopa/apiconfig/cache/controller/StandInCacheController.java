package it.gov.pagopa.apiconfig.cache.controller;

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
  @Override
  String stakeholder() {
    return "node";
  }

  @Override
  String[] keys() {
    return new String[]{
            Constants.version,
            Constants.stations,
    };
  }
}
