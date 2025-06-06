prefix    = "pagopa"
env       = "prod"
env_short = "p"
location_short  = "weu"
domain  = "apiconfig"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Prod"
  Owner       = "pagoPA"
  Source      = "https://github.com/pagopa/pagopa-api-config-cache"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

apim_dns_zone_prefix               = "platform"
external_domain                    = "pagopa.it"
hostname = "weuprod.nodo.internal.platform.pagopa.it"
