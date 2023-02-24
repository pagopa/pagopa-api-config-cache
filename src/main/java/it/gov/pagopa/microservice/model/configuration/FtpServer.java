package it.gov.pagopa.microservice.model.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * FtpServer
 */
@Getter
@Setter

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonPropertyOrder({"host","port","enabled","username","password","root_path","service","type","in_path","out_path","history_path"})
public class FtpServer {

  @JsonProperty(value = "host", required = true)
  private String host;

  @JsonProperty(value = "port", required = true)
  private Integer port;

  @JsonProperty(value = "enabled", required = true)
  private Boolean enabled;

  @JsonProperty(value = "username", required = true)
  private String username;

  @JsonProperty(value = "password", required = true)
  private String password;

  @JsonProperty(value = "root_path", required = true)
  private String rootPath;

  @JsonProperty(value = "service", required = true)
  private String service;

  @JsonProperty(value = "type", required = true)
  private String type;

  @JsonProperty(value = "in_path", required = true)
  private String inPath;

  @JsonProperty(value = "out_path", required = true)
  private String outPath;

  @JsonProperty(value = "history_path", required = true)
  private String historyPath;


  @JsonIgnore
  public String getIdentifier() {
    return "" + this.hashCode();
  }

}
