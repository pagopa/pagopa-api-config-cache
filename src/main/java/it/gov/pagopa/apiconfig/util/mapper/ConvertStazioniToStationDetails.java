package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.model.node.v1.configuration.Protocol;
import it.gov.pagopa.apiconfig.model.node.v1.creditorinstitution.Station;
import it.gov.pagopa.apiconfig.starter.entity.Stazioni;
import it.gov.pagopa.apiconfig.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;


public class ConvertStazioniToStationDetails implements Converter<Stazioni, Station> {

  @Override
  public Station convert(MappingContext<Stazioni, Station> context) {
    Stazioni source = context.getSource();
    return Station.builder()
        .stationCode(source.getIdStazione())
        .enabled(source.getEnabled())
        .version(source.getVersione())
        .password(CommonUtil.deNull(source.getPassword()))
        .protocol(Protocol.fromValue(source.getProtocollo()))
        .ip(CommonUtil.deNull(source.getIp()))
        .port(source.getPorta())
        .service(CommonUtil.deNull(source.getServizio()))
        .pofService(CommonUtil.deNull(source.getServizioPof()))
        .nmpService(CommonUtil.deNull(source.getServizioPof()))
        .protocol4Mod(
            source.getProtocollo4Mod() != null ? Protocol.fromValue(source.getProtocollo4Mod())
                : null)
        .brokerCode(CommonUtil.deNull(source.getIntermediarioPa().getIdIntermediarioPa()))
        .ip4Mod(CommonUtil.deNull(source.getIp4Mod()))
        .port4Mod(source.getPorta4Mod())
        .service4Mod(CommonUtil.deNull(source.getServizio4Mod()))
        .redirectProtocol(source.getRedirectProtocollo() != null ? Protocol.fromValue(
            source.getRedirectProtocollo()) : null)
        .redirectIp(CommonUtil.deNull(source.getRedirectIp()))
        .redirectPort(source.getRedirectPorta())
        .redirectPath(CommonUtil.deNull(source.getRedirectPath()))
        .redirectQueryString(CommonUtil.deNull(source.getRedirectQueryString()))
        .proxyEnabled(CommonUtil.deNull(source.getProxyEnabled()))
        .proxyHost(CommonUtil.deNull(source.getProxyHost()))
        .proxyPort(source.getProxyPort())
        .proxyUsername(CommonUtil.deNull(source.getProxyUsername()))
        .proxyPassword(CommonUtil.deNull(source.getProxyPassword()))
        .targetHost(CommonUtil.deNull(source.getTargetHost()))
        .targetPort(source.getTargetPort())
        .targetPath(CommonUtil.deNull(source.getTargetPath()))
        .threadNumber(source.getNumThread())
        .timeoutA(source.getTimeoutA())
        .timeoutB(source.getTimeoutB())
        .timeoutC(source.getTimeoutC())
        .rtInstantaneousDispatch(source.getInvioRtIstantaneo())
        .primitiveVersion(source.getVersionePrimitive())
        .build();
  }
}
