#
# Build
#

FROM maven:3.8.4-jdk-11-slim as buildtime
ENV STARTER_URL="https://cesarecaccuri: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@maven.pkg.github.com/pagopa/pagopa-api-config-starter"
WORKDIR /build
COPY . .
RUN mvn package -DskipTests=true

FROM adoptopenjdk/openjdk11:alpine-jre as builder
COPY --from=buildtime /build/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ghcr.io/pagopa/docker-base-springboot-openjdk11:v1.0.1@sha256:bbbe948e91efa0a3e66d8f308047ec255f64898e7f9250bdb63985efd3a95dbf
COPY --chown=spring:spring  --from=builder dependencies/ ./
COPY --chown=spring:spring  --from=builder snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
COPY --chown=spring:spring  --from=builder spring-boot-loader/ ./
COPY --chown=spring:spring  --from=builder application/ ./

EXPOSE 8080
