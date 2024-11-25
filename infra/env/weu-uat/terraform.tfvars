prefix    = "pagopa"
env       = "uat"
env_short = "u"
location_short  = "weu"
domain  = "apiconfig"

tags = {
  CreatedBy   = "Terraform"
  Environment = "Uat"
  Owner       = "pagoPA"
  Source      = "https://github.com/pagopa/pagopa-api-config-cache"
  CostCenter  = "TS310 - PAGAMENTI & SERVIZI"
}

apim_dns_zone_prefix               = "uat.platform"
external_domain                    = "pagopa.it"
hostname = "weuuat.nodo.internal.uat.platform.pagopa.it"
