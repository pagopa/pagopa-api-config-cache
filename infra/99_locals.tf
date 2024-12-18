locals {
  product = "${var.prefix}-${var.env_short}"
  project = "${var.prefix}-${var.env_short}-${var.location_short}-${var.domain}"

  apim = {
    name       = "${local.product}-apim"
    rg         = "${local.product}-api-rg"
    product_id = "apiconfig-cache"
    hostname = "api.${var.apim_dns_zone_prefix}.${var.external_domain}"
  }
  apim_x_node_product_id = "apim_for_node"
  cfg_x_node_product_id = "cfg-for-node"
  technical_support_product_id = "technical_support_api"
  cache_export_product_id = "apiconfig-cache-export"

  oracle   = "o"
  postgres = "p"

  apiconfig_cache_locals = {
    hostname = var.env == "prod" ? "weuprod.apiconfig.internal.platform.pagopa.it" : "weu${var.env}.apiconfig.internal.${var.env}.platform.pagopa.it"

    product_id            = "apiconfig-cache"
    display_name          = "API Config Cache"
    description           = "Management APIs to configure pagoPA cache"
    subscription_required = true
    subscription_limit    = 1000

    path        = "api-config-cache"
    service_url = null

    pagopa_tenant_id = data.azurerm_client_config.current.tenant_id
  }

  apiconfig_cache_replica_locals = {
    hostname = var.env == "prod" ? "weuprod.apiconfig.internal.platform.pagopa.it" : "weu${var.env}.apiconfig.internal.${var.env}.platform.pagopa.it"

    product_id            = "apiconfig-cache-replica"
    display_name          = "API Config Cache Replica"
    description           = "Management APIs to configure pagoPA cache replica"
    subscription_required = true
    subscription_limit    = 1000

    path_apim   = "api-config-cache"
    service_url = null

    pagopa_tenant_id = data.azurerm_client_config.current.tenant_id
  }

  apiconfig_cache_export_locals = {
    hostname = var.env == "prod" ? "weuprod.apiconfig.internal.platform.pagopa.it" : "weu${var.env}.apiconfig.internal.${var.env}.platform.pagopa.it"

    display_name          = "API Config Cache - Export"
    description           = "Export API of configuration about pagoPA cache"
    subscription_required = true
    subscription_limit    = 1000

    path_apim   = "api-config-cache/export"
    path        = "api-config-cache"
    service_url = null

    pagopa_tenant_id = data.azurerm_client_config.current.tenant_id
  }

  apiconfig_cache_replica_export_locals = {
    hostname = var.env == "prod" ? "weuprod.apiconfig.internal.platform.pagopa.it" : "weu${var.env}.apiconfig.internal.${var.env}.platform.pagopa.it"

    display_name          = "API Config Cache Replica - Export"
    description           = "Export API of configuration about pagoPA cache replica"
    subscription_required = true
    subscription_limit    = 1000

    path_apim   = "api-config-cache/export"
    path        = "api-config-cache"
    service_url = null

    pagopa_tenant_id = data.azurerm_client_config.current.tenant_id
  }

}

