# PostgreSQL
resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_export_node_api_p" {
  name                = "${var.env_short}-${var.domain}-cache-export-${local.postgres}-api"
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = "${local.apiconfig_cache_export_locals.display_name} - ${local.postgres}"
  versioning_scheme   = "Segment"
}

module "apim_api_apiconfig_cache_export_node_api_v1_p" {
  source                = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v8.5.0"
  name                = "${var.env_short}-${var.domain}-cache-export-${local.postgres}-api"
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id, local.technical_support_product_id, local.cache_export_product_id]
  subscription_required = local.apiconfig_cache_export_locals.subscription_required

  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_export_node_api_p.id
  api_version    = "v1"

  description  = local.apiconfig_cache_export_locals.description
  display_name = "${local.apiconfig_cache_export_locals.display_name} - ${local.postgres}"

  path        = "${local.apiconfig_cache_export_locals.path_apim}/${local.postgres}"
  protocols   = ["https"]
  service_url = local.apiconfig_cache_export_locals.service_url

  content_format = "openapi"
  content_value = templatefile("../openapi/openapi_export.json", {
    host    = local.apim.hostname
    service = "node-${local.postgres}"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = local.apiconfig_cache_export_locals.hostname
    hostname = format("%s/%s", local.apiconfig_cache_export_locals.hostname, "${local.apiconfig_cache_export_locals.path}/${local.postgres}")
  })
}

resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_replica_export_node_api_p" {
  count               = var.env_short == "u" ? 1 : 0

  name                = "${var.env_short}-${var.domain}-cache-replica-export-${local.postgres}-api"
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = "${local.apiconfig_cache_replica_export_locals.display_name} - ${local.postgres}r"
  versioning_scheme   = "Segment"
}

module "apim_api_apiconfig_cache_replica_export_node_api_v1_p" {
  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v8.5.0"
  count               = var.env_short == "u" ? 1 : 0

  name                = "${var.env_short}-${var.domain}-cache-replica-export-${local.postgres}-api"
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id, local.technical_support_product_id, local.cache_export_product_id]
  subscription_required = local.apiconfig_cache_replica_export_locals.subscription_required

  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_replica_export_node_api_p[0].id
  api_version    = "v1"

  description  = local.apiconfig_cache_replica_export_locals.description
  display_name = "${local.apiconfig_cache_replica_export_locals.display_name} - ${local.postgres}"

  path        = format("%s/%s", local.apiconfig_cache_replica_export_locals.path_apim, "${local.postgres}r")
  protocols   = ["https"]
  service_url = local.apiconfig_cache_replica_export_locals.service_url

  content_format = "openapi"
  content_value = templatefile("../openapi/openapi_export.json", {
    host    = local.apim.hostname
    service = "node-${local.postgres}-replica"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = format("%s/%s/%s", local.apiconfig_cache_replica_export_locals.hostname, local.apiconfig_cache_replica_export_locals.path, "${local.postgres}r")
  })
}
