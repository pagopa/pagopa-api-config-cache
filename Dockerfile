#
# Build
#

FROM openjdk:11-jdk-slim as buildtime
ENV STARTER_URL="https://cesarecaccuri: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@maven.pkg.github.com/pagopa/pagopa-api-config-starter"
WORKDIR /build
COPY pom.xml pom.xml
COPY mvnw ./mvnw
COPY .mvn ./.mvn
RUN ./mvnw dependency:resolve -DskipTests=true
COPY . .
RUN ./mvnw package -DskipTests=true

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
