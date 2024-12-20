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

# Oracle

#resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_export_node_api_o" {
#  name                = format("%s-apicfg-cache-export-%s-api", var.env_short, local.oracle)
#  resource_group_name = local.apim.rg
#  api_management_name = local.apim.name
#  display_name        = "${local.apiconfig_cache_export_locals.display_name} - Node ${local.oracle}"
#  versioning_scheme   = "Segment"
#}
#
#module "apim_api_apiconfig_cache_export_node_api_v1_o" {
#  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v8.5.0"
#
#  name                  = format("%s-apicfg-cache-export-%s-api", local.project, local.oracle)
#  api_management_name   = local.apim.name
#  resource_group_name   = local.apim.rg
#  product_ids           = [local.apim.product_id, local.technical_support_product_id]
#  subscription_required = local.apiconfig_cache_export_locals.subscription_required
#
#  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_export_node_api_o.id
#  api_version    = "v1"
#
#  description  = local.apiconfig_cache_export_locals.description
#  display_name = "${local.apiconfig_cache_export_locals.display_name} - Node ${local.oracle}"
#
#  path        = "${local.apiconfig_cache_export_locals.path_apim}/${local.oracle}"
#  protocols   = ["https"]
#  service_url = local.apiconfig_cache_export_locals.service_url
#
#  content_format = "openapi"
#  content_value = templatefile("../openapi/openapi_export.json", {
#    host    = local.apim.hostname
#    service = "node-${local.oracle}"
#  })
#
#  xml_content = templatefile("./policy/_base_policy.xml", {
#    hostname = local.apiconfig_cache_locals.hostname
#    hostname = format("%s/%s", local.apiconfig_cache_export_locals.hostname, "${local.apiconfig_cache_export_locals.path}/${local.oracle}")
#  })
#}

resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_replica_export_node_api_p" {
  count               = var.env_short == "u" ? 1 : 0

  name                = "${var.env_short}-${var.domain}-cache-replica-export-${local.postgres}-api"
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = "${local.apiconfig_cache_replica_export_locals.display_name} - ${local.postgres}r"
  versioning_scheme   = "Segment"
}

#resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_replica_export_node_api_o" {
#  count               = var.env_short == "p" ? 0 : 1
#  name                = format("%s-apicfg-cache-replica-export-%s-api", var.env_short, "o")
#  resource_group_name = local.apim.rg
#  api_management_name = local.apim.name
#  display_name        = "${local.apiconfig_cache_replica_export_locals.display_name} - Node o"
#  versioning_scheme   = "Segment"
#}

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

#module "apim_api_apiconfig_cache_replica_export_node_api_v1_o" {
#  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v8.5.0"
#  count  = var.env_short == "p" ? 0 : 1
#
#  name                  = format("%s-apicfg-cache-replica-export-%s-api", local.project, "o")
#  api_management_name   = local.apim.name
#  resource_group_name   = local.apim.rg
#  product_ids           = [local.apim.product_id, local.technical_support_product_id]
#  subscription_required = local.apiconfig_cache_replica_export_locals.subscription_required
#
#  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_replica_export_node_api_o[0].id
#  api_version    = "v1"
#
#  description  = local.apiconfig_cache_replica_export_locals.description
#  display_name = "${local.apiconfig_cache_replica_export_locals.display_name} - Node o"
#
#  path        = format("%s/%s", local.apiconfig_cache_replica_export_locals.path_apim, "or")
#  protocols   = ["https"]
#  service_url = local.apiconfig_cache_replica_export_locals.service_url
#
#  content_format = "openapi"
#  content_value = templatefile("../openapi/openapi_export.json", {
#    host    = local.apim.hostname
#    service = "node-o-replica"
#  })
#
#  xml_content = templatefile("./policy/_base_policy.xml", {
#    hostname = format("%s/%s/%s", local.apiconfig_cache_replica_export_locals.hostname, local.apiconfig_cache_replica_export_locals.path, "or")
#  })
#}

