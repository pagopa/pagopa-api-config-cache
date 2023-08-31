#!/bin/bash
set -e

if [[ "$(pwd)" =~ .*"openapi".* ]]; then
    cd ..
fi

mvn test -Dtest=OpenApiGenerationTest

# UI mode http://localhost:8080/swagger-ui/index.html
