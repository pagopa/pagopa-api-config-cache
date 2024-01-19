package it.gov.pagopa.apiconfig.cache.controller.stakeholders;

import it.gov.pagopa.apiconfig.cache.controller.CacheController;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/node/cache/schemas")
@Validated
@Slf4j
public class NodeCacheController extends CacheController {

  public static String[] KEYS = new String[]{
    Constants.version,
            Constants.creditorInstitutions,
            Constants.creditorInstitutionBrokers,
            Constants.stations,
            Constants.creditorInstitutionStations,
            Constants.encodings,
            Constants.creditorInstitutionEncodings,
            Constants.ibans,
            Constants.creditorInstitutionInformations,
            Constants.psps,
            Constants.pspBrokers,
            Constants.paymentTypes,
            Constants.pspChannelPaymentTypes,
            Constants.plugins,
            Constants.pspInformationTemplates,
            Constants.pspInformations,
            Constants.channels,
            Constants.cdsServices,
            Constants.cdsSubjects,
            Constants.cdsSubjectServices,
            Constants.cdsCategories,
            Constants.configurations,
            Constants.ftpServers,
            Constants.languages,
            Constants.gdeConfigurations,
            Constants.metadataDict
  };

  @Override
  protected String[] keys() {
    return NodeCacheController.KEYS;
  }
}
