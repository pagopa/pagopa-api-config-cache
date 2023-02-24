package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.entity.Canali;
import it.gov.pagopa.microservice.model.configuration.Protocol;
import it.gov.pagopa.microservice.model.psp.Channel;
import it.gov.pagopa.microservice.model.psp.PaymentModel;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertCanaliToChannelDetails implements Converter<Canali, Channel> {

    @Override
    public Channel convert(MappingContext<Canali, Channel> context) {
        Canali source = context.getSource();
        return Channel.builder()
                .channelCode(source.getIdCanale())
                .description(source.getDescrizione())
                .enabled(source.getEnabled())
                .ip(source.getIp())
                .password(source.getPassword())
                .port(source.getPorta())
                .protocol(Protocol.fromValue(source.getProtocollo()))
                .service(source.getServizio())
                .brokerPspCode(source.getFkIntermediarioPsp().getIdIntermediarioPsp())
                .proxyEnabled(source.getProxyEnabled())
                .proxyHost(source.getProxyHost())
                .proxyPort(source.getProxyPort())
                .proxyUsername(source.getProxyUsername())
                .proxyPassword(source.getProxyPassword())
                .targetHost(source.getTargetHost())
                .targetPort(source.getTargetPort())
                .targetPath(source.getTargetPath())
                .threadNumber(source.getNumThread())
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .newFaultCode(source.getUseNewFaultCode())
                .npmService(source.getServizioNmp()).redirectIp(source.getRedirectIp())
                .redirectPath(source.getRedirectPath())
                .redirectPort(source.getRedirectPorta())
                .redirectQueryString(source.getRedirectQueryString())
                .redirectProtocol(source.getRedirectProtocollo() != null ? Protocol.fromValue(source.getRedirectProtocollo()) : null)
                .paymentModel(getPaymentModel(source.getModelloPagamento()))
                .rtPush(source.getRtPush())
                .recovery(source.getRecovery())
                .digitalStampBrand(source.getMarcaBolloDigitale())
                .agid(source.getAgidChannel())
                .primitiveVersion(source.getVersionePrimitive())
                .flagIo(source.getFlagIo())
            .servPlugin(source.getIdServPlugin()).build();
    }

    private PaymentModel getPaymentModel(String modelloPagamento) {
        return PaymentModel.fromDatabaseValue(modelloPagamento);
    }


}
