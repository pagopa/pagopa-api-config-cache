package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.model.node.v1.common.Connection;
import it.gov.pagopa.apiconfig.model.node.v1.common.Protocol;
import it.gov.pagopa.apiconfig.model.node.v1.common.Proxy;
import it.gov.pagopa.apiconfig.model.node.v1.common.Redirect;
import it.gov.pagopa.apiconfig.model.node.v1.common.Service;
import it.gov.pagopa.apiconfig.model.node.v1.common.Timeouts;
import it.gov.pagopa.apiconfig.model.node.v1.psp.Channel;
import it.gov.pagopa.apiconfig.model.node.v1.psp.PaymentModel;
import it.gov.pagopa.apiconfig.starter.entity.CanaliView;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertCanaliToChannelDetails implements Converter<CanaliView, Channel> {

  @Override
  public Channel convert(MappingContext<CanaliView, Channel> context) {
    CanaliView source = context.getSource();
    Proxy proxy = null;
    if(source.getProxyEnabled() !=null && source.getProxyEnabled()){
      proxy = Proxy.builder()
          .proxyHost(source.getProxyHost())
          .proxyPort(source.getProxyPort())
          .proxyUsername(source.getProxyUsername())
          .proxyPassword(source.getProxyPassword()).build();
    }

    return Channel.builder()
        .channelCode(source.getIdCanale())
        .description(source.getDescrizione())
        .enabled(source.getEnabled())
        .connection(Connection
            .builder()
            .protocol(Protocol.fromValue(source.getProtocollo()))
            .ip(source.getIp())
            .port(source.getPorta())
            .build())
        .password(source.getPassword())
        .service(
            Service.builder()
                .path(source.getServizio())
                .targetHost(source.getTargetHost())
                .targetPort(source.getTargetPort())
                .targetPath(source.getTargetPath())
                .build())
        .nmpService(
            Service.builder()
                .path(source.getServizioNmp())
                .targetHost(source.getTargetHostNmp())
                .targetPort(source.getTargetPortNmp())
                .targetPath(source.getTargetPathNmp())
                .build())
        .brokerPspCode(source.getIntermediarioPsp().getIdIntermediarioPsp())
        .proxy(proxy)
        .threadNumber(source.getNumThread())
        .timeouts(
            Timeouts.builder()
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .build())
        .newFaultCode(source.getUseNewFaultCode())
        .redirect(Redirect.builder()
            .protocol(Protocol.fromValue(source.getRedirectProtocollo()))
            .ip(source.getRedirectIp())
            .port(source.getRedirectPort())
            .path(source.getRedirectPath())
            .queryString(source.getRedirectQueryString()).build())
        .paymentModel(source.getModelloPagamento())
        .rtPush(source.getRtPush())
        .recovery(source.getRecovery())
        .digitalStamp(source.getMarcaBolloDigitale())
        .agid(source.getAgidChannel())
        .primitiveVersion(source.getVersionePrimitive())
        .flagIo(source.getFlagIo())
        .servPlugin(source.getIdServPlugin())
        .build();
  }

  private PaymentModel getPaymentModel(String modelloPagamento) {
    return PaymentModel.fromDatabaseValue(modelloPagamento);
  }


}
