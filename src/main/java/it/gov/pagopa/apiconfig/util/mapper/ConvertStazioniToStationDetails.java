package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.model.node.v1.common.Connection;
import it.gov.pagopa.apiconfig.model.node.v1.common.Protocol;
import it.gov.pagopa.apiconfig.model.node.v1.common.Proxy;
import it.gov.pagopa.apiconfig.model.node.v1.common.Redirect;
import it.gov.pagopa.apiconfig.model.node.v1.common.Service;
import it.gov.pagopa.apiconfig.model.node.v1.common.Timeouts;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertStazioniToStationDetails implements Converter<Stazioni, Station> {

  @Override
  public Station convert(MappingContext<Stazioni, Station> context) {
    Stazioni source = context.getSource();
    Proxy proxy = null;
    if (source.getProxyEnabled()) {
      proxy = Proxy.builder()
          .proxyHost(source.getProxyHost())
          .proxyPort(source.getProxyPort())
          .proxyUsername(source.getProxyUsername())
          .proxyPassword(source.getProxyPassword()).build();
    }

    return Station.builder()
        .stationCode(source.getIdStazione())
        .enabled(source.getEnabled())
        .version(source.getVersione())
        .password(source.getPassword())
        .connection(
            Connection.builder()
                .protocol(Protocol.fromValue(source.getProtocollo()))
                .ip(source.getIp())
                .port(source.getPorta())
                .build())
        .service(
            Service.builder()
                .path(source.getServizio())
                .targetHost(source.getTargetHost())
                .targetPort(source.getTargetPort())
                .targetPath(source.getTargetPath())
                .build())
        .pofService(
            Service.builder()
                .path(source.getServizioPof())
                .targetHost(source.getTargetHostPof())
                .targetPort(source.getTargetPortPof())
                .targetPath(source.getTargetPathPof())
                .build())
        .mod4Service(
            Service.builder().path(source.getServizio4Mod()).build()
        )
        .mod4connection(
            Connection.builder()
                .protocol(Protocol.fromValue(source.getProtocollo4Mod()))
                .ip(source.getIp4Mod())
                .port(source.getPorta4Mod())
                .build())
        .brokerCode(source.getIntermediarioPa().getIdIntermediarioPa())
        .redirect(Redirect.builder()
            .protocol(Protocol.fromValue(source.getRedirectProtocollo()))
            .ip(source.getRedirectIp())
            .port(source.getRedirectPort())
            .path(source.getRedirectPath())
            .queryString(source.getRedirectQueryString()).build())
        .proxy(proxy)
        .threadNumber(source.getNumThread())
        .timeouts(
            Timeouts.builder()
                .timeoutA(source.getTimeoutA())
                .timeoutB(source.getTimeoutB())
                .timeoutC(source.getTimeoutC())
                .build())
        .rtInstantaneousDispatch(source.getInvioRtIstantaneo())
        .primitiveVersion(source.getVersionePrimitive())
        .build();
  }
}
