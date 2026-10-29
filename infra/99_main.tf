terraform {
  required_version = ">= 1.9.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4"
    }
    azuread = {
      source  = "hashicorp/azuread"
      version = "<= 2.47.0"
    }
    azapi = {
      source  = "Azure/azapi"
      version = "<= 1.3.0"
    }
  }

  backend "azurerm" {}
}

provider "azurerm" {
  features {}
}

provider "azapi" {}

data "azurerm_subscription" "current" {}

data "azurerm_client_config" "current" {}

module "__v4__" {
  # v9.0.1
  source = "git::https://github.com/pagopa/terraform-azurerm-v4?ref=498aa770edfe95264936e9e8f7e209322e4a3820"
}