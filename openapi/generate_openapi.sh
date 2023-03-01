#!/bin/bash
set -e

# install api-spec-converter if not present
if [ $(npm list -g | grep -c api-spec-converter) -eq 0 ]; then
  npm install -g api-spec-converter
fi

if ! $(curl --output /dev/null --silent --head --fail http://localhost:8080/actuator/info); then
  # create containers
  cd ../docker || exit
  sh ./run_docker.sh "$1" "$2"
  cd ../openapi || exit
fi


# save openapi
curl http://localhost:8080/v3/api-docs/nodev1 > ./openapi_nodev1.json
curl http://localhost:8080/v3/api-docs/nodev2 > ./openapi_nodev2.json
curl http://localhost:8080/v3/api-docs/verifier > ./openapi_verifier.json

# UI mode http://localhost:8080/swagger-ui/index.html
