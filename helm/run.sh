#!/bin/bash

DIR=.
NAME=cache
NAMESPACE=apiconfig

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
        --weight)
            shift
            weight="$1"
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

if [ -n "$install" ]; then
  if [ ! -n "$version" ]; then
    echo "Error: Version parameter required with --install." >&2
    exit 1
  fi
fi

if [ -n "$canary" ] && [ -n "$install" ]; then
  if [ ! -n "$weight" ]; then
    echo "Error: Weight parameter required with --install and --canary flag." >&2
    exit 1
  fi
fi

if [ -n "$update" ]; then
  echo "Updating dependencies"
  helm dep update $DIR
fi

if [ -n "$install" ] && [ "$install" -eq 1 ]; then
  if [ -n "$canary" ]; then
    echo "Installing canary version $version,weight $weight"
    helm upgrade --namespace $NAMESPACE --install --values $DIR/values-dev.yaml \
      --set oracle.enabled=false \
      --set postgresql.image.tag=$version \
      --set postgresql.canaryDelivery.create="true" \
      --set postgresql.canaryDelivery.ingress.weightPercent="$weight" \
      $NAME-canary $DIR
    exit 0
  else
    echo "Installing stable version $version"
    helm upgrade --namespace $NAMESPACE --install --values $DIR/values-dev.yaml \
      --set oracle.enabled=false \
      --set postgresql.image.tag=$version \
      $NAME $DIR
    exit 0
  fi
fi

if [ -n "$uninstall" ]; then
  if [ -n "$canary" ]; then
    echo "Uninstalling canary"
    helm uninstall --namespace $NAMESPACE --wait $NAME-canary
  else
    echo "Uninstalling stable"
    helm uninstall --namespace $NAMESPACE --wait $NAME
  fi
fi

exit 0