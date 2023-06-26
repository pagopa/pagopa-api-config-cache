package it.gov.pagopa.apiconfig.cache.controller;

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
}
