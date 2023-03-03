#!/bin/bash

ENV=uat
IMAGE_VERSION="3.12.0-SNAPSHOT"
CANARY_IMAGE_VERSION="3.12.0-SNAPSHOT"

FILE_CONFIG_PATH_LOGBACK=helm/config/${ENV}/logback.xml
FILE_CONFIG_PATH_CONFIG=helm/config/${ENV}/config-app.conf

clean (){
  #$> clean "helm/nodo"
  CHART_PATH=$1
  rm -rf ${CHART_PATH}/charts
  rm -f ${CHART_PATH}/Chart.lock
}

fixVersion () {
  #$> fixAppVersion "helm/nodo/Chart.yaml"
  CHART_FILE=$1
  if [[ -f "$CHART_FILE" ]]; then
    yq -i ".appVersion = \"${IMAGE_VERSION}\"" "$CHART_FILE"
  fi
}

########## APP ############
#helm uninstall --namespace nodo ndp
clean "helm/nodo"

helm repo add microservice-chart https://pagopa.github.io/aks-microservice-chart-blueprint
helm dep build helm/nodo

fixVersion "helm/nodo/Chart.yaml"

helm upgrade --install --namespace nodo \
    --values helm/nodo/values-${ENV}.yaml \
    --set 'nodo.image.tag'="${CANARY_IMAGE_VERSION}" \
    --set 'nodo.canaryDelivery.create'="false" \
    --set 'nodo.canaryDelivery.deployment.image.tag'="${CANARY_IMAGE_VERSION}" \
    --set 'nodo.canaryDelivery.ingress.canary.weightPercent'="50" \
    --set-file 'nodo.fileConfig.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'nodo.fileConfig.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    ndp helm/nodo --dry-run > test.yml


########## CRON ############
helm uninstall --namespace nodo-cron ndp-cron
clean "helm/nodo-cron"

helm repo add cron-chart https://pagopa.github.io/aks-cron-chart-blueprint
helm dep build helm/nodo-cron

fixVersion "helm/nodo-cron/Chart.yaml"

helm upgrade --install --namespace nodo-cron \
    --values helm/nodo-cron/values-${ENV}.yaml \
    --set 'cj-annullamento-rpt.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-annullamento-rpt.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-annullamento-rpt.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-ftp-upload.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-ftp-upload.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-ftp-upload.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-genera-rend-bollo.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-genera-rend-bollo.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-genera-rend-bollo.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-mod3-cancel-v1.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-mod3-cancel-v1.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-mod3-cancel-v1.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-mod3-cancel-v2.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-mod3-cancel-v2.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-mod3-cancel-v2.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-pa-invia-rt.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-pa-invia-rt.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-pa-invia-rt.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-pa-invia-rt-recovery.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-pa-invia-rt-recovery.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-pa-invia-rt-recovery.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-pa-retry-invia-rt-neg.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-pa-retry-invia-rt-neg.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-pa-retry-invia-rt-neg.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-pa-send-rt.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-pa-send-rt.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-pa-send-rt.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-pos-retry-send-payment-res.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-pos-retry-send-payment-res.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-pos-retry-send-payment-res.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-psp-chiedi-avanz-rpt.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-psp-chiedi-avanz-rpt.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-psp-chiedi-avanz-rpt.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-psp-chiedi-lista-rt.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-psp-chiedi-lista-rt.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-psp-chiedi-lista-rt.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-psp-retry-ack-negative.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-psp-retry-ack-negative.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-psp-retry-ack-negative.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-retry-pa-attiva-rpt.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-retry-pa-attiva-rpt.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-retry-pa-attiva-rpt.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    --set 'cj-rt-pull-recovery-push.image.tag'="${IMAGE_VERSION}" \
    --set-file 'cj-rt-pull-recovery-push.fileConfig.files.logback\.xml'="${FILE_CONFIG_PATH_LOGBACK}" \
    --set-file 'cj-rt-pull-recovery-push.fileConfig.files.config-app\.conf'="${FILE_CONFIG_PATH_CONFIG}" \
    ndp-cron helm/nodo-cron --dry-run > test-cron.yml
