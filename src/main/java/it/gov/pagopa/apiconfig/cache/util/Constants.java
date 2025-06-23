package it.gov.pagopa.apiconfig.cache.util;

import lombok.experimental.UtilityClass;

import java.util.Base64;

@UtilityClass
public class Constants {

  public static final Base64.Encoder ENCODER = Base64.getEncoder();

  public static final String HEADER_REQUEST_ID = "X-Request-Id";

  public static final String HEADER_X_CACHE_ID = "X-CACHE-ID";
  public static final String HEADER_X_CACHE_TIMESTAMP = "X-CACHE-TIMESTAMP";
  public static final String HEADER_X_CACHE_VERSION = "X-CACHE-VERSION";
  public static final String GZIP_JSON = "GZIP_JSON";
  public static final String GZIP_JSON_V1 = GZIP_JSON +"-v1";
  public static final String FULL = "full";
  public static final String FULL_INFORMATION = "FULL";
  public static final String NA = "na";
  public static final String VERSION = "version";
  public static final String TIMESTAMP = "timestamp";
  public static final String CACHE_VERSION = "cacheVersion";
  public static final String CREDITOR_INSTITUTIONS = "creditorInstitutions";
  public static final String CREDITOR_INSTITUTION_BROKERS = "creditorInstitutionBrokers";
  public static final String STATIONS = "stations";
  public static final String CREDITOR_INSTITUTION_STATIONS = "creditorInstitutionStations";
  public static final String MAINTENANCE_STATIONS = "maintenanceStations";
  public static final String ENCODINGS = "encodings";
  public static final String CREDITOR_INSTITUTION_ENCODINGS = "creditorInstitutionEncodings";
  public static final String IBANS = "ibans";
  public static final String CREDITOR_INSTITUTION_INFORMATIONS = "creditorInstitutionInformations";
  public static final String PSPS = "psps";
  public static final String PSP_BROKERS = "pspBrokers";
  public static final String PAYMENT_TYPES = "paymentTypes";
  public static final String PSP_CHANNEL_PAYMENT_TYPES = "pspChannelPaymentTypes";
  public static final String PLUGINS = "plugins";
  public static final String PSP_INFORMATION_TEMPLATES = "pspInformationTemplates";
  public static final String PSP_INFORMATIONS = "pspInformations";
  public static final String CHANNELS = "channels";
  public static final String CDS_SERVICES = "cdsServices";
  public static final String CDS_SUBJECTS = "cdsSubjects";
  public static final String CDS_SUBJECT_SERVICES = "cdsSubjectServices";
  public static final String CDS_CATEGORIES = "cdsCategories";
  public static final String CONFIGURATIONS = "configurations";
  public static final String FTP_SERVERS = "ftpServers";
  public static final String LANGUAGES = "languages";
  public static final String GDE_CONFIGURATIONS = "gdeConfigurations";
  public static final String METADATA_DICT = "metadataDict";

  public static String[] keys={
          CREDITOR_INSTITUTIONS,
          CREDITOR_INSTITUTION_BROKERS,
          STATIONS,
          CREDITOR_INSTITUTION_STATIONS,
          ENCODINGS,
          CREDITOR_INSTITUTION_ENCODINGS,
          IBANS,
          CREDITOR_INSTITUTION_INFORMATIONS,
          PSPS,
          PSP_BROKERS,
          PAYMENT_TYPES,
          PSP_CHANNEL_PAYMENT_TYPES,
          PLUGINS,
          PSP_INFORMATION_TEMPLATES,
          PSP_INFORMATIONS,
          CHANNELS,
          CDS_SERVICES,
          CDS_SUBJECTS,
          CDS_SUBJECT_SERVICES,
          CDS_CATEGORIES,
          CONFIGURATIONS,
          FTP_SERVERS,
          LANGUAGES,
          GDE_CONFIGURATIONS,
          METADATA_DICT
  };
}
