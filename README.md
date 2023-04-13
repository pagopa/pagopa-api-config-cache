# pagoPA API-Config Cache

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-api-config-cache&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=pagopa_pagopa-api-config-cache)

API-Config Cache aims to generate customized cache data stored on the Redis cache for the specific stakeholder needs.

- [Technology Stack](#technology-stack)
- [Start project locally 🚀 `TODO`](#start-project-locally-----todo-)
    * [Prerequisites](#prerequisites)
    * [Run docker container](#run-docker-container)
- [Develop locally 💻](#develop-locally---)
    * [Prerequisites](#prerequisites-1)
    * [Run the project](#run-the-project)
    * [Spring Profiles](#spring-profiles)
    * [Testing 🧪](#testing---)
        + [Unit testing](#unit-testing)
- [Contributors 👥](#contributors---)
    * [Mainteiners](#mainteiners)

---

## API Documentation 📖

See the OpenApi 3 for [Node here](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-api-config-cache/main/openapi/openapi_nodev1.json).

See the OpenApi 3 for [Verifier here](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-api-config-cache/main/openapi/openapi_verifier.json).

---

## Technology Stack

- Java 11
- Spring Boot
- Spring Web
- Hibernate
- JPA
- Redis

---

## Start project locally 🚀 `TODO`

### Prerequisites

- docker 

### Run docker container

from `./docker` directory

`sh ./run_docker.sh dev`

ℹ️ Note: for PagoPa ACR is required the login `az acr login -n <acr-name>`

---

## Develop locally 💻

### Prerequisites

- git
- maven
- jdk-11

### Run the project

Start the springboot application with this command:

`mvn spring-boot:run -Dspring-boot.run.profiles=local`

`mvn spring-boot:start -Dspring-boot.run.profiles=local`

### Spring Profiles

- **local**: to develop locally.
- _default (no profile set)_: The application gets the properties from the environment (for Azure).

### Testing 🧪

#### Unit testing

To run the **Junit** tests:

`mvn clean verify`

---

## Contributors 👥

Made with ❤️ by PagoPa S.p.A.

### Mainteiners

See `CODEOWNERS` file
