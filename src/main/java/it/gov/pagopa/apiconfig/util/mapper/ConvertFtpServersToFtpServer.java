package it.gov.pagopa.apiconfig.util.mapper;

import it.gov.pagopa.apiconfig.starter.entity.FtpServers;
import it.gov.pagopa.apiconfig.model.node.v1.configuration.FtpServer;
import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

public class ConvertFtpServersToFtpServer implements Converter<FtpServers, FtpServer> {

  @Override
  public FtpServer convert(MappingContext<FtpServers, FtpServer> mappingContext) {
    FtpServers ftpServers = mappingContext.getSource();
    return FtpServer.builder()
        .id(ftpServers.getId())
        .host(ftpServers.getHost())
        .port(ftpServers.getPort())
        .username(ftpServers.getUsername())
        .password(ftpServers.getPassword())
        .rootPath(ftpServers.getRootPath())
        .service(ftpServers.getService())
        .type(ftpServers.getType())
        .inPath(ftpServers.getInPath())
        .outPath(ftpServers.getOutPath())
        .historyPath(ftpServers.getHistoryPath())
        .enabled(ftpServers.getEnabled())
        .build();
  }
}
