package it.gov.pagopa.microservice.util;

import it.gov.pagopa.microservice.entity.CanaleTipoVersamento;
import it.gov.pagopa.microservice.entity.Canali;
import it.gov.pagopa.microservice.entity.CdsServizio;
import it.gov.pagopa.microservice.entity.CdsSoggettoServizio;
import it.gov.pagopa.microservice.entity.Codifiche;
import it.gov.pagopa.microservice.entity.CodifichePa;
import it.gov.pagopa.microservice.entity.FtpServers;
import it.gov.pagopa.microservice.entity.IbanValidiPerPa;
import it.gov.pagopa.microservice.entity.IntermediariPa;
import it.gov.pagopa.microservice.entity.IntermediariPsp;
import it.gov.pagopa.microservice.entity.Pa;
import it.gov.pagopa.microservice.entity.Psp;
import it.gov.pagopa.microservice.entity.Stazioni;
import it.gov.pagopa.microservice.entity.TipiVersamento;
import it.gov.pagopa.microservice.entity.WfespPluginConf;
import it.gov.pagopa.microservice.model.cds.CdsService;
import it.gov.pagopa.microservice.model.cds.CdsSubjectService;
import it.gov.pagopa.microservice.model.configuration.FtpServer;
import it.gov.pagopa.microservice.model.configuration.PaymentType;
import it.gov.pagopa.microservice.model.configuration.Plugin;
import it.gov.pagopa.microservice.model.creditorinstitution.BrokerCreditorInstitution;
import it.gov.pagopa.microservice.model.creditorinstitution.CreditorInstitution;
import it.gov.pagopa.microservice.model.creditorinstitution.CreditorInstitutionEncoding;
import it.gov.pagopa.microservice.model.creditorinstitution.Encoding;
import it.gov.pagopa.microservice.model.creditorinstitution.Iban;
import it.gov.pagopa.microservice.model.creditorinstitution.Station;
import it.gov.pagopa.microservice.model.psp.BrokerPsp;
import it.gov.pagopa.microservice.model.psp.Channel;
import it.gov.pagopa.microservice.model.psp.PaymentServiceProvider;
import it.gov.pagopa.microservice.util.mapper.ConvertCanaleTipoVersamentoToPaymentType;
import it.gov.pagopa.microservice.util.mapper.ConvertCanaliToChannelDetails;
import it.gov.pagopa.microservice.util.mapper.ConvertCodifichePaToEncoding;
import it.gov.pagopa.microservice.util.mapper.ConvertCodificheToEncoding;
import it.gov.pagopa.microservice.util.mapper.ConvertFtpServersToFtpServer;
import it.gov.pagopa.microservice.util.mapper.ConvertIbanValidiPerPaToIban;
import it.gov.pagopa.microservice.util.mapper.ConvertIntermediariPaToBrokerDetails;
import it.gov.pagopa.microservice.util.mapper.ConvertIntermediariPspToBrokerPspDetails;
import it.gov.pagopa.microservice.util.mapper.ConvertPaToCreditorInstitutionDetails;
import it.gov.pagopa.microservice.util.mapper.ConvertPaymentTypeToString;
import it.gov.pagopa.microservice.util.mapper.ConvertPaymentTypeToTipiVersamento;
import it.gov.pagopa.microservice.util.mapper.ConvertPspToPaymentServiceProviderDetails;
import it.gov.pagopa.microservice.util.mapper.ConvertStazioniToStationDetails;
import it.gov.pagopa.microservice.util.mapper.ConvertTipiVersamentoToPaymentType;
import it.gov.pagopa.microservice.util.mapper.ConvertWfespPluginConfToWfespPluginConf;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class ConfigMapper {

  public ModelMapper modelMapper() {

    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

    Converter<Pa, CreditorInstitution> convertPaToCreditorInstitutionDetails = new ConvertPaToCreditorInstitutionDetails();
    Converter<Stazioni, Station> convertStazioniToStationDetails = new ConvertStazioniToStationDetails();
    Converter<CodifichePa, CreditorInstitutionEncoding> convertCodifichePaToEncoding = new ConvertCodifichePaToEncoding();
    Converter<Codifiche, Encoding> convertCodificheToEncoding = new ConvertCodificheToEncoding();
    Converter<IbanValidiPerPa, Iban> convertIbanValidiPerPaToIban = new ConvertIbanValidiPerPaToIban();
    Converter<IntermediariPa, BrokerCreditorInstitution> convertIntermediariPaToBrokerDetails = new ConvertIntermediariPaToBrokerDetails();
    Converter<Psp, PaymentServiceProvider> convertPspToPaymentServiceProvider = new ConvertPspToPaymentServiceProviderDetails();
    Converter<IntermediariPsp, BrokerPsp> convertIntermediariPspToBrokerPspDetails = new ConvertIntermediariPspToBrokerPspDetails();
    Converter<Canali, Channel> convertCanaliToChannelDetails = new ConvertCanaliToChannelDetails();
    Converter<CanaleTipoVersamento, PaymentType> convertCanaleTipoVersamentoToPaymentType = new ConvertCanaleTipoVersamentoToPaymentType();
    Converter<WfespPluginConf, Plugin> convertConfWfespPluginConf = new ConvertWfespPluginConfToWfespPluginConf();
    Converter<FtpServers, FtpServer> convertFtpServersFtpServer = new ConvertFtpServersToFtpServer();
    Converter<TipiVersamento, PaymentType> convertTipiVersamentoPaymentType = new ConvertTipiVersamentoToPaymentType();
    Converter<PaymentType, String> convertPaymentTypeString = new ConvertPaymentTypeToString();
    Converter<PaymentType, TipiVersamento> convertPaymentTypeTipiVersamento = new ConvertPaymentTypeToTipiVersamento();

    Converter<CdsServizio, CdsService> convertCdsServizioCdsCatService = new Converter<CdsServizio, CdsService>() {
      public CdsService convert(MappingContext<CdsServizio, CdsService> mappingContext) {
        CdsServizio source = mappingContext.getSource();
        return CdsService.builder()
            .serviceId(source.getIdServizio())
            .description(source.getDescrizioneServizio())
            .version(source.getVersion())
            .category(source.getCategoria().getDescription())
            .referenceXsd(source.getXsdRiferimento())
            .build();
      }
    };
    Converter<CdsSoggettoServizio, CdsSubjectService> convertCdsSoggettoServizioCdsSubjectService = new Converter<CdsSoggettoServizio, CdsSubjectService>() {
      public CdsSubjectService convert(
          MappingContext<CdsSoggettoServizio, CdsSubjectService> mappingContext) {
        CdsSoggettoServizio source = mappingContext.getSource();
        return CdsSubjectService.builder()
            .service(source.getServizio().getIdServizio())
            .subjectServiceId(source.getIdSoggettoServizio())
            .startDate(source.getDataInizioValidita())
            .endDate(source.getDataFineValidita())
            .fee(source.getCommissione())
            .subject(source.getSoggetto().getCreditorInstitutionCode())
            .build();
      }
    };

    mapper.createTypeMap(Pa.class, CreditorInstitution.class)
        .setConverter(convertPaToCreditorInstitutionDetails);
    mapper.createTypeMap(Stazioni.class, Station.class)
        .setConverter(convertStazioniToStationDetails);
    mapper.createTypeMap(CodifichePa.class, CreditorInstitutionEncoding.class)
        .setConverter(convertCodifichePaToEncoding);
    mapper.createTypeMap(Codifiche.class, Encoding.class).setConverter(convertCodificheToEncoding);
    mapper.createTypeMap(IbanValidiPerPa.class, Iban.class)
        .setConverter(convertIbanValidiPerPaToIban);
    mapper.createTypeMap(IntermediariPa.class, BrokerCreditorInstitution.class)
        .setConverter(convertIntermediariPaToBrokerDetails);
    mapper.createTypeMap(Psp.class, PaymentServiceProvider.class)
        .setConverter(convertPspToPaymentServiceProvider);
    mapper.createTypeMap(IntermediariPsp.class, BrokerPsp.class)
        .setConverter(convertIntermediariPspToBrokerPspDetails);
    mapper.createTypeMap(Canali.class, Channel.class).setConverter(convertCanaliToChannelDetails);
    mapper.createTypeMap(CanaleTipoVersamento.class, PaymentType.class)
        .setConverter(convertCanaleTipoVersamentoToPaymentType);
    mapper.createTypeMap(WfespPluginConf.class, Plugin.class)
        .setConverter(convertConfWfespPluginConf);
    mapper.createTypeMap(FtpServers.class, FtpServer.class)
        .setConverter(convertFtpServersFtpServer);
    mapper.createTypeMap(TipiVersamento.class, PaymentType.class)
        .setConverter(convertTipiVersamentoPaymentType);
    mapper.createTypeMap(PaymentType.class, String.class).setConverter(convertPaymentTypeString);
    mapper.createTypeMap(PaymentType.class, TipiVersamento.class)
        .setConverter(convertPaymentTypeTipiVersamento);

    mapper.createTypeMap(CdsServizio.class, CdsService.class)
        .setConverter(convertCdsServizioCdsCatService);
    mapper.createTypeMap(CdsSoggettoServizio.class, CdsSubjectService.class)
        .setConverter(convertCdsSoggettoServizioCdsSubjectService);

    return mapper;
  }
}
