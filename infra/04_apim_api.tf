# PostgreSQL PagoPA

resource "azurerm_api_management_api_version_set" "api_apiconfig_cache_node_api_p" {
  name                = "${var.env_short}-${var.domain}-cache-node-${local.postgres}-api"
  resource_group_name = local.apim.rg
  api_management_name = local.apim.name
  display_name        = "${local.apiconfig_cache_locals.display_name} - Node ${local.postgres}"
  versioning_scheme   = "Segment"
}

module "apim_api_apiconfig_cache_node_api_v1_p" {
  source                = "git::https://github.com/pagopa/terraform-azurerm-v3.git//api_management_api?ref=v8.5.0"
  name                  = "${var.env_short}-${var.domain}-cache-node-${local.postgres}-api"
  api_management_name   = local.apim.name
  resource_group_name   = local.apim.rg
  product_ids           = [local.apim.product_id, local.apim_x_node_product_id, local.cfg_x_node_product_id]
  subscription_required = local.apiconfig_cache_locals.subscription_required

  version_set_id = azurerm_api_management_api_version_set.api_apiconfig_cache_node_api_p.id
  api_version    = "v1"

  description  = local.apiconfig_cache_locals.description
  display_name = "${local.apiconfig_cache_locals.display_name} - Node ${local.postgres}"

  path        = "${local.apiconfig_cache_locals.path}/${local.postgres}"
  protocols   = ["https"]
  service_url = local.apiconfig_cache_locals.service_url

  content_format = "openapi"
  content_value = templatefile("../openapi/openapi.json", {
    host    = local.apim.hostname
    service = "node-${local.postgres}"
  })

  xml_content = templatefile("./policy/_base_policy.xml", {
    hostname = local.apiconfig_cache_locals.hostname
    hostname = format("%s/%s", local.apiconfig_cache_locals.hostname, "${local.apiconfig_cache_locals.path}/${local.postgres}")
  })
}