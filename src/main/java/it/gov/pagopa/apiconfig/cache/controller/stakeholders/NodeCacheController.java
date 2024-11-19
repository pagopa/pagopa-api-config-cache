package it.gov.pagopa.apiconfig.cache.controller.stakeholders;

import it.gov.pagopa.apiconfig.cache.controller.StakeholderController;
import it.gov.pagopa.apiconfig.cache.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stakeholders/node/cache/schemas")
@Validated
@Slf4j
public class NodeCacheController extends StakeholderController {

  @Value("#{'${saveDB}'=='true'}")
  private Boolean saveDB;

  public static String[] KEYS = new String[]{
            Constants.VERSION,
            Constants.CREDITOR_INSTITUTIONS,
            Constants.CREDITOR_INSTITUTION_BROKERS,
            Constants.STATIONS,
            Constants.CREDITOR_INSTITUTION_STATIONS,
            Constants.ENCODINGS,
            Constants.CREDITOR_INSTITUTION_ENCODINGS,
            Constants.IBANS,
            Constants.CREDITOR_INSTITUTION_INFORMATIONS,
            Constants.PSPS,
            Constants.PSP_BROKERS,
            Constants.PAYMENT_TYPES,
            Constants.PSP_CHANNEL_PAYMENT_TYPES,
            Constants.PLUGINS,
            Constants.PSP_INFORMATION_TEMPLATES,
            Constants.PSP_INFORMATIONS,
            Constants.CHANNELS,
            Constants.CDS_SERVICES,
            Constants.CDS_SUBJECTS,
            Constants.CDS_SUBJECT_SERVICES,
            Constants.CDS_CATEGORIES,
            Constants.CONFIGURATIONS,
            Constants.FTP_SERVERS,
            Constants.LANGUAGES,
            Constants.GDE_CONFIGURATIONS,
            Constants.METADATA_DICT
  };

  public static String STAKEHOLDER = "node";

  @Override
  protected String[] keys() {
    return NodeCacheController.KEYS;
  }

  @Override
  protected String stakeholder() {
    return NodeCacheController.STAKEHOLDER;
  }

  @Override
  protected boolean saveOnDB() {
    return saveDB;
  }

}
