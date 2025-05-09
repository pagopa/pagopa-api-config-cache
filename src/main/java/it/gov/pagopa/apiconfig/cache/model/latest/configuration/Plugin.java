package it.gov.pagopa.apiconfig.cache.model.latest.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Plugin implements Serializable {

  @JsonProperty(value = "id_serv_plugin", required = true)
  private String idServPlugin;

  @JsonProperty(value = "pag_const_string_profile")
  private String profiloPagConstString;

  @JsonProperty(value = "pag_soap_rule_profile")
  private String profiloPagSoapRule;

  @JsonProperty(value = "pag_rpt_xpath_profile")
  private String profiloPagRptXpath;

  @JsonProperty(value = "id_bean")
  private String idBean;
}
