package it.gov.pagopa.apiconfig.cache.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String HEADER_REQUEST_ID = "X-Request-Id";
  public static final String GZIP_JSON_V1 = "GZIP_JSON-v1";

 public static String FULL = "full";


 public static String version = "version";
 public static String creditorInstitutions = "creditorInstitutions";
 public static String creditorInstitutionBrokers = "creditorInstitutionBrokers";
 public static String stations = "stations";
 public static String creditorInstitutionStations = "creditorInstitutionStations";
 public static String encodings = "encodings";
 public static String creditorInstitutionEncodings = "creditorInstitutionEncodings";
 public static String ibans = "ibans";
 public static String creditorInstitutionInformations = "creditorInstitutionInformations";
 public static String psps = "psps";
 public static String pspBrokers = "pspBrokers";
 public static String paymentTypes = "paymentTypes";
 public static String pspChannelPaymentTypes = "pspChannelPaymentTypes";
 public static String plugins = "plugins";
 public static String pspInformationTemplates = "pspInformationTemplates";
 public static String pspInformations = "pspInformations";
 public static String channels = "channels";
 public static String cdsServices = "cdsServices";
 public static String cdsSubjects = "cdsSubjects";
 public static String cdsSubjectServices = "cdsSubjectServices";
 public static String cdsCategories = "cdsCategories";
 public static String configurations = "configurations";
 public static String ftpServers = "ftpServers";
 public static String languages = "languages";
 public static String gdeConfigurations = "gdeConfigurations";
 public static String metadataDict = "metadataDict";

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
