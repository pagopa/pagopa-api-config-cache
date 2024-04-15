package it.gov.pagopa.apiconfig.cache.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String headerRequestId = "X-Request-Id";
  public static final String gzipJsonV1 = "GZIP_JSON-v1";

 public static final String full = "full";
 public static final String fullInformation = "FULL";
 public static final String na = "na";
 public static final String version = "version";
 public static final String timestamp = "timestamp";
 public static final String cacheVersion = "cacheVersion";
 public static final String creditorInstitutions = "creditorInstitutions";
 public static final String creditorInstitutionBrokers = "creditorInstitutionBrokers";
 public static final String stations = "stations";
 public static final String creditorInstitutionStations = "creditorInstitutionStations";
 public static final String encodings = "encodings";
 public static final String creditorInstitutionEncodings = "creditorInstitutionEncodings";
 public static final String ibans = "ibans";
 public static final String creditorInstitutionInformations = "creditorInstitutionInformations";
 public static final String psps = "psps";
 public static final String pspBrokers = "pspBrokers";
 public static final String paymentTypes = "paymentTypes";
 public static final String pspChannelPaymentTypes = "pspChannelPaymentTypes";
 public static final String plugins = "plugins";
 public static final String pspInformationTemplates = "pspInformationTemplates";
 public static final String pspInformations = "pspInformations";
 public static final String channels = "channels";
 public static final String cdsServices = "cdsServices";
 public static final String cdsSubjects = "cdsSubjects";
 public static final String cdsSubjectServices = "cdsSubjectServices";
 public static final String cdsCategories = "cdsCategories";
 public static final String configurations = "configurations";
 public static final String ftpServers = "ftpServers";
 public static final String languages = "languages";
 public static final String gdeConfigurations = "gdeConfigurations";
 public static final String metadataDict = "metadataDict";

  public static String[] keys={
          creditorInstitutions,
          creditorInstitutionBrokers,
          stations,
          creditorInstitutionStations,
          encodings,
          creditorInstitutionEncodings,
          ibans,
          creditorInstitutionInformations,
          psps,
          pspBrokers,
          paymentTypes,
          pspChannelPaymentTypes,
          plugins,
          pspInformationTemplates,
          pspInformations,
          channels,
          cdsServices,
          cdsSubjects,
          cdsSubjectServices,
          cdsCategories,
          configurations,
          ftpServers,
          languages,
          gdeConfigurations,
          metadataDict
  };
}
