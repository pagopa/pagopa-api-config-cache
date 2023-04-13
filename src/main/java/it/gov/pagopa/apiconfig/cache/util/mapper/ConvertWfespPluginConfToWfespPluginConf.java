package it.gov.pagopa.apiconfig.cache.util.mapper;

import it.gov.pagopa.apiconfig.cache.model.node.v1.configuration.Plugin;
import it.gov.pagopa.apiconfig.starter.entity.WfespPluginConf;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertWfespPluginConfToWfespPluginConf implements Converter<WfespPluginConf, Plugin> {

  @Override
  public Plugin convert(MappingContext<WfespPluginConf, Plugin> mappingContext) {
    WfespPluginConf wfespPluginConf = mappingContext.getSource();
    return Plugin.builder()
        .idBean(wfespPluginConf.getIdBean())
        .idServPlugin(wfespPluginConf.getIdServPlugin())
        .profiloPagConstString(wfespPluginConf.getProfiloPagConstString())
        .profiloPagSoapRule(wfespPluginConf.getProfiloPagSoapRule())
        .profiloPagRptXpath(wfespPluginConf.getProfiloPagRptXpath())
        .build();
  }
}
