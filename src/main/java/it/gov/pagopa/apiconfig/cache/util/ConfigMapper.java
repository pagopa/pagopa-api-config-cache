package it.gov.pagopa.apiconfig.cache.util;

import it.gov.pagopa.apiconfig.cache.entity.CdsSoggettoServizioCustom;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsService;
import it.gov.pagopa.apiconfig.cache.model.node.v1.cds.CdsSubjectService;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.ConfigurationKey;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.FtpServer;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.PaymentType;
import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.Plugin;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Encoding;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Iban;
import it.gov.pagopa.apiconfig.cache.model.node.v1.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.BrokerPsp;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.Channel;
import it.gov.pagopa.apiconfig.cache.model.node.v1.psp.PaymentServiceProvider;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertCanaleTipoVersamentoToPaymentType;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertCanaliToChannelDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertCdsServizioCdsCatService;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertCdsSoggettoServizioCdsSubjectService;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertCodifichePaToEncoding;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertCodificheToEncoding;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertConfiguration;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertFtpServersToFtpServer;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertIbanValidiPerPaToIban;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertIntermediariPaToBrokerDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertIntermediariPspToBrokerPspDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertPaToCreditorInstitutionDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertPaymentTypeToString;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertPaymentTypeToTipiVersamento;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertPspToPaymentServiceProviderDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertStazioniToStationDetails;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertTipiVersamentoToPaymentType;
import it.gov.pagopa.apiconfig.cache.util.mapper.ConvertWfespPluginConfToWfespPluginConf;
import it.gov.pagopa.apiconfig.starter.entity.CanaleTipoVersamento;
import it.gov.pagopa.apiconfig.starter.entity.CanaliView;
import it.gov.pagopa.apiconfig.starter.entity.CdsServizio;
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
    Converter<CanaleTipoVersamento, PaymentType> convertCanaleTipoVersamentoToPaymentType =
        new ConvertCanaleTipoVersamentoToPaymentType();
    Converter<WfespPluginConf, Plugin> convertConfWfespPluginConf =
        new ConvertWfespPluginConfToWfespPluginConf();
    Converter<FtpServers, FtpServer> convertFtpServersFtpServer =
        new ConvertFtpServersToFtpServer();
    Converter<TipiVersamento, PaymentType> convertTipiVersamentoPaymentType =
        new ConvertTipiVersamentoToPaymentType();
    Converter<PaymentType, String> convertPaymentTypeString = new ConvertPaymentTypeToString();
    Converter<PaymentType, TipiVersamento> convertPaymentTypeTipiVersamento =
        new ConvertPaymentTypeToTipiVersamento();

    Converter<CdsServizio, CdsService> convertCdsServizioCdsCatService =
        new ConvertCdsServizioCdsCatService();
    Converter<CdsSoggettoServizioCustom, CdsSubjectService> convertCdsSoggettoServizioCdsSubjectService =
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
        .createTypeMap(CanaleTipoVersamento.class, PaymentType.class)
        .setConverter(convertCanaleTipoVersamentoToPaymentType);
    mapper
        .createTypeMap(WfespPluginConf.class, Plugin.class)
        .setConverter(convertConfWfespPluginConf);
    mapper
        .createTypeMap(FtpServers.class, FtpServer.class)
        .setConverter(convertFtpServersFtpServer);
    mapper
        .createTypeMap(TipiVersamento.class, PaymentType.class)
        .setConverter(convertTipiVersamentoPaymentType);
    mapper.createTypeMap(PaymentType.class, String.class).setConverter(convertPaymentTypeString);
    mapper
        .createTypeMap(PaymentType.class, TipiVersamento.class)
        .setConverter(convertPaymentTypeTipiVersamento);

    mapper
        .createTypeMap(CdsServizio.class, CdsService.class)
        .setConverter(convertCdsServizioCdsCatService);
    mapper
        .createTypeMap(CdsSoggettoServizioCustom.class, CdsSubjectService.class)
        .setConverter(convertCdsSoggettoServizioCdsSubjectService);

    mapper
        .createTypeMap(ConfigurationKeys.class, ConfigurationKey.class)
        .setConverter(convertConfiguration);

    return mapper;
  }
}
