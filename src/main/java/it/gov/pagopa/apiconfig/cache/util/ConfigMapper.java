package it.gov.pagopa.apiconfig.cache.util;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsService;
import it.gov.pagopa.apiconfig.cache.model.latest.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.FtpServer;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.PaymentType;
import it.gov.pagopa.apiconfig.cache.model.latest.configuration.Plugin;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.cache.model.latest.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.Channel;
import it.gov.pagopa.apiconfig.cache.model.latest.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertCanaliToChannelDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertCdsServizioCdsCatService;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertCdsSoggettoServizioCdsSubjectService;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertCodifichePaToEncoding;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertCodificheToEncoding;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertConfiguration;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertFtpServersToFtpServer;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertIbanValidiPerPaToIban;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertIntermediariPaToBrokerDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertIntermediariPspToBrokerPspDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertPaToCreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertPspToPaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertStazioniToStationDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertTipiVersamentoToPaymentType;
import it.gov.pagopa.apiconfig.cache.util.mapper.entitiesToLatest.ConvertWfespPluginConfToWfespPluginConf;
import it.gov.pagopa.apiconfig.starter.entity.CanaliView;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
import it.gov.pagopa.apiconfig.starter.entity.CdsSoggettoServizio;
import it.gov.pagopa.apiconfig.starter.entity.Codifiche;
import it.gov.pagopa.apiconfig.starter.entity.CodifichePa;
import it.gov.pagopa.apiconfig.starter.entity.ConfigurationKeys;
import it.gov.pagopa.apiconfig.starter.entity.FtpServers;
import it.gov.pagopa.apiconfig.starter.entity.IbanValidiPerPa;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPa;
import it.gov.pagopa.apiconfig.starter.entity.IntermediariPsp;
import it.gov.pagopa.apiconfig.starter.entity.Pa;
import it.gov.pagopa.apiconfig.starter.entity.Psp;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.starter.entity.TipiVersamento;
import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class ConfigMapper {

  public ModelMapper modelMapper() {

    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    Converter<Pa, CreditorInstitution> convertPaToCreditorInstitutionDetails =
        new ConvertPaToCreditorInstitutionDetails();
    Converter<ConfigurationKeys, ConfigurationKey> convertConfiguration =
        new ConvertConfiguration();
    Converter<Stazioni, Station> convertStazioniToStationDetails =
        new ConvertStazioniToStationDetails();
    Converter<CodifichePa, CreditorInstitutionEncoding> convertCodifichePaToEncoding =
        new ConvertCodifichePaToEncoding();
    Converter<Codifiche, Encoding> convertCodificheToEncoding = new ConvertCodificheToEncoding();
    Converter<IbanValidiPerPa, Iban> convertIbanValidiPerPaToIban =
        new ConvertIbanValidiPerPaToIban();
    Converter<IntermediariPa, BrokerCreditorInstitution> convertIntermediariPaToBrokerDetails =
        new ConvertIntermediariPaToBrokerDetails();
    Converter<Psp, PaymentServiceProvider> convertPspToPaymentServiceProvider =
        new ConvertPspToPaymentServiceProviderDetails();
    Converter<IntermediariPsp, BrokerPsp> convertIntermediariPspToBrokerPspDetails =
        new ConvertIntermediariPspToBrokerPspDetails();
    Converter<CanaliView, Channel> convertCanaliToChannelDetails =
        new ConvertCanaliToChannelDetails();
    Converter<WfespPluginConf, Plugin> convertConfWfespPluginConf =
        new ConvertWfespPluginConfToWfespPluginConf();
    Converter<FtpServers, FtpServer> convertFtpServersFtpServer =
        new ConvertFtpServersToFtpServer();
    Converter<TipiVersamento, PaymentType> convertTipiVersamentoPaymentType =
        new ConvertTipiVersamentoToPaymentType();
    Converter<CdsServizio, CdsService> convertCdsServizioCdsCatService =
        new ConvertCdsServizioCdsCatService();
    Converter<CdsSoggettoServizio, CdsSubjectService> convertCdsSoggettoServizioCdsSubjectService =
        new ConvertCdsSoggettoServizioCdsSubjectService();

    mapper
        .createTypeMap(Pa.class, CreditorInstitution.class)
        .setConverter(convertPaToCreditorInstitutionDetails);
    mapper
        .createTypeMap(Stazioni.class, Station.class)
        .setConverter(convertStazioniToStationDetails);
    mapper
        .createTypeMap(CodifichePa.class, CreditorInstitutionEncoding.class)
        .setConverter(convertCodifichePaToEncoding);
    mapper.createTypeMap(Codifiche.class, Encoding.class).setConverter(convertCodificheToEncoding);
    mapper
        .createTypeMap(IbanValidiPerPa.class, Iban.class)
        .setConverter(convertIbanValidiPerPaToIban);
    mapper
        .createTypeMap(IntermediariPa.class, BrokerCreditorInstitution.class)
        .setConverter(convertIntermediariPaToBrokerDetails);
    mapper
        .createTypeMap(Psp.class, PaymentServiceProvider.class)
        .setConverter(convertPspToPaymentServiceProvider);
    mapper
        .createTypeMap(IntermediariPsp.class, BrokerPsp.class)
        .setConverter(convertIntermediariPspToBrokerPspDetails);
    mapper
        .createTypeMap(CanaliView.class, Channel.class)
        .setConverter(convertCanaliToChannelDetails);
    mapper
        .createTypeMap(WfespPluginConf.class, Plugin.class)
        .setConverter(convertConfWfespPluginConf);
    mapper
        .createTypeMap(FtpServers.class, FtpServer.class)
        .setConverter(convertFtpServersFtpServer);
    mapper
        .createTypeMap(TipiVersamento.class, PaymentType.class)
        .setConverter(convertTipiVersamentoPaymentType);
    mapper
        .createTypeMap(CdsServizio.class, CdsService.class)
        .setConverter(convertCdsServizioCdsCatService);
    mapper
        .createTypeMap(CdsSoggettoServizio.class, CdsSubjectService.class)
        .setConverter(convertCdsSoggettoServizioCdsSubjectService);

    mapper
        .createTypeMap(ConfigurationKeys.class, ConfigurationKey.class)
        .setConverter(convertConfiguration);

    return mapper;
  }
}
