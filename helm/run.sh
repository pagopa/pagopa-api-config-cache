#!/bin/bash

DIR=.
NAMESPACE=apiconfig

location=weu
env=dev
NAME=cache

usage() {
  echo "Usage: $0 [--update] [--canary] [--install] [--uninstall] [--weight <percentage>] [--version <version>]" 1>&2;
  echo ""
  echo "Options:"
  echo "--update                Run helm dep update before installing."
  echo "--canary                Install canary version of the application."
  echo "--install               Install the application."
  echo "--uninstall             Uninstall the application."
  echo "--weight <percentage>   Specify a weight percentage for the canary version."
  echo "--version <version>     Specify a version for the deploy."
  exit 1;
}

while [[ "$#" -gt 0 ]]; do
    case $1 in
        --update)
            update=1
            ;;
        --canary)
            canary=1
            ;;
        --install)
            install=1
            ;;
        --uninstall)
            uninstall=1
            ;;
        --it)
            env="it"
            NAME="$NAME"-replica
            ;;
        --weight)
            shift
            weight="$1"
            ;;
        --weu)
            location="weu"
            ;;
        --neu)
            location="neu"
            ;;
        --version)
            shift
            version="$1"
            ;;
        -h|--help)
            usage
            exit 0
            ;;

        *)
            echo "Unknown parameter passed: $1" >&2
            exit 1
            ;;
    esac
    shift
done

if [ "$install" == 1 ]; then
  if [ ! -n "$version" ]; then
    echo "Error: Version parameter required with --install." >&2
    exit 1
  fi
fi

if [ -n "$update" ]; then
  echo "Updating dependencies"
  helm dep update $DIR
fi

if [ "$location" == "weu" ]; then
  valuesFile=$location-$env/values-$env.yaml
  context="pagopa-d-$location-dev-aks"
else
  valuesFile=$location-$env/values-$env.yaml
  context="pagopa-d-$location-dev-aks"
fi

echo "Using
context      | $context
valuesFile   | $valuesFile
"


kubectl config use-context $context

if [ "$install" == 1 ]; then
  if [ "$canary" == 1 ]; then
    echo "Installing canary version $version"
    helm upgrade --dry-run --namespace $NAMESPACE --install --values $valuesFile \
      --set postgresql.image.tag=$version \
      --set postgresql.canaryDelivery.create="true" \
      --set oracle.image.tag=$version \
      --set oracle.canaryDelivery.create="true" \
      $NAME-canary $DIR > dry-github.yaml
    exit 0
  else
    echo "Installing stable version $version"
    helm upgrade --namespace $NAMESPACE --install --values $valuesFile \
      --set postgresql.image.tag=$version \
      --set postgresql.enabled=true \
      --set oracle.image.tag=$version \
      --set oracle.enabled=true \
      --set oracledev.image.tag=$version \
      --set oracledev.enabled=false \
      $NAME $DIR
    exit 0
  fi
fi

if [ "$uninstall" == 1 ]; then
  if [ "$canary" == 1 ]; then
    echo "Uninstalling canary"
    helm uninstall --namespace $NAMESPACE --wait $NAME-canary
  else
    echo "Uninstalling stable"
    helm uninstall --namespace $NAMESPACE --wait $NAME
  fi
fi

exit 0