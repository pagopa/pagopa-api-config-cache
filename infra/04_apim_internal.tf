resource "azurerm_api_management_api_operation_policy" "internal_set_cache" {

  api_name            = module.apim_api_apiconfig_cache_node_api_v1_p.name
  api_management_name = local.apim.name
  resource_group_name = local.apim.rg
  operation_id        = "setCache"

  #tfsec:ignore:GEN005
  xml_content = file("./policy/internal_set_cache.xml")
}