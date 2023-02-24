package it.gov.pagopa.microservice.util.mapper;

import it.gov.pagopa.microservice.model.configuration.Plugin;
import it.gov.pagopa.microservice.entity.WfespPluginConf;
import it.gov.pagopa.microservice.util.CommonUtil;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertWfespPluginConfToWfespPluginConf implements Converter<WfespPluginConf, Plugin> {
    @Override
    public Plugin convert(MappingContext<WfespPluginConf, Plugin> mappingContext) {
        WfespPluginConf wfespPluginConf = mappingContext.getSource();
        return Plugin.builder()
                .idBean(CommonUtil.deNull(wfespPluginConf.getIdBean()))
                .idServPlugin(wfespPluginConf.getIdServPlugin())
                .profiloPagConstString(CommonUtil.deNull(wfespPluginConf.getProfiloPagConstString()))
                .profiloPagSoapRule(CommonUtil.deNull(wfespPluginConf.getProfiloPagSoapRule()))
                .profiloPagRptXpath(CommonUtil.deNull(wfespPluginConf.getProfiloPagRptXpath()))
                .build();
    }
}
