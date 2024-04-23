# Oracle Nexi
resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_node_api_o" {
  name                = format("%s-apiconfig-cache-node-%s-api", var.env_short, local.oracle)
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = "${local.apiconfig_cache_locals.display_name} - Node ${local.oracle}"
  versioning_scheme   = "Segment"
}

module "apim_api_apiconfig_cache_node_api_v1_o" {
  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v5.1.0"

  name                  = format("%s-apiconfig-cache-node-%s-api", local.project, local.oracle)
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id, local.apim_x_node_product_id]
  subscription_required = local.apiconfig_cache_locals.subscription_required

  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_node_api_o.id
  api_version    = "v1"

  description  = local.apiconfig_cache_locals.description
  display_name = "${local.apiconfig_cache_locals.display_name} - Node ${local.oracle}"

  path        = "${local.apiconfig_cache_locals.path}/${local.oracle}"
  protocols   = ["https"]
  service_url = local.apiconfig_cache_locals.service_url

  content_format = "openapi"
  content_value = templatefile("../openapi/openapi.json", {
    host    = local.apim.hostname
    service = "node-${local.oracle}"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = local.apiconfig_cache_locals.hostname
    hostname = format("%s/%s", local.apiconfig_cache_locals.hostname, "${local.apiconfig_cache_locals.path}/${local.oracle}")
  })
}

resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_node_nexi_api" {
  count               = var.env_short != "p" ? 1 : 0
  name                = format("%s-apiconfig-cache-node-%s-api", var.env_short, "nexi")
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = "${local.apiconfig_cache_locals.display_name} - Node Nexi"
  versioning_scheme   = "Segment"
}

module "apim_api_apiconfig_cache_node_nexi_api_dev_v1" {
  count  = var.env_short == "d" ? 1 : 0
  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v6.4.1"

  name                  = format("%s-apiconfig-cache-node-%s-api", local.project, "nexi")
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id, local.apim_x_node_product_id]
  subscription_required = local.apiconfig_cache_locals.subscription_required

  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_node_nexi_api[0].id
  api_version    = "v1"

  description  = local.apiconfig_cache_locals.description
  display_name = "${local.apiconfig_cache_locals.display_name} - Node Nexi"

  path        = "${local.apiconfig_cache_locals.path}/odev"
  protocols   = ["https"]
  service_url = local.apiconfig_cache_locals.service_url

  content_format = "openapi"
  content_value = templatefile("../openapi/openapi.json", {
    host    = local.apim.hostname
    service = "node-nexi"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = local.apiconfig_cache_locals.hostname
    hostname = format("%s/%s", local.apiconfig_cache_locals.hostname, "${local.apiconfig_cache_locals.path}dev/o")
  })
}

resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_replica_node_api_o" {
  count               = var.env_short == "p" ? 0 : 1
  name                = format("%s-apiconfig-cache-replica-node-%s-api", var.env_short, "o")
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = "${local.apiconfig_cache_replica_locals.display_name} - Node o"
  versioning_scheme   = "Segment"
}

module "apim_api_apiconfig_cache_replica_node_api_v1_o" {
  source = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v6.4.1"
  count  = var.env_short == "p" ? 0 : 1

  name                  = format("%s-apiconfig-cache-replica-node-%s-api", local.project, "o")
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id, local.apim_x_node_product_id]
  subscription_required = local.apiconfig_cache_replica_locals.subscription_required

  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_replica_node_api_o[0].id
  api_version    = "v1"

  description  = local.apiconfig_cache_replica_locals.description
  display_name = "${local.apiconfig_cache_replica_locals.display_name} - Node o"

  path        = format("%s/%s", local.apiconfig_cache_replica_locals.path_apim, "or")
  protocols   = ["https"]
  service_url = local.apiconfig_cache_replica_locals.service_url

  content_format = "openapi"
  content_value = templatefile("../openapi/openapi.json", {
    host    = local.apim.hostname
    service = "node-o-replica"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = format("%s/%s%s/%s", local.apiconfig_cache_replica_locals.hostname, local.apiconfig_cache_replica_locals.path_apim, "dev", "o")
  })
}

