#
# Build
#
FROM maven:3.9.6-amazoncorretto-17-al2023@sha256:4c8bd9ec72b372f587f7b9d92564a307e4f5180b7ec08455fb346617bae1757e as buildtime
WORKDIR /build
COPY . .
RUN --mount=type=secret,id=GH_TOKEN,dst=/tmp/secret_token export GITHUB_TOKEN_READ_PACKAGES="$(cat /tmp/secret_token)" \
  && mvn clean package -Dmaven.test.skip=true

FROM amazoncorretto:17.0.9-alpine3.18@sha256:df48bf2e183230040890460ddb4359a10aa6c7aad24bd88899482c52053c7e17 AS builder
COPY --from=buildtime /build/target/*.jar application.jar
RUN java -Djarmode=layertools -jar application.jar extract

FROM ghcr.io/pagopa/docker-base-springboot-openjdk17:v2.2.0@sha256:b866656c31f2c6ebe6e78b9437ce930d6c94c0b4bfc8e9ecc1076a780b9dfb18

COPY --chown=spring:spring  --from=builder dependencies/ ./
COPY --chown=spring:spring  --from=builder snapshot-dependencies/ ./
# https://github.com/moby/moby/issues/37965#issuecomment-426853382
RUN true
COPY --chown=spring:spring  --from=builder spring-boot-loader/ ./
COPY --chown=spring:spring  --from=builder application/ ./

EXPOSE 8080

# this mode is required in order to inject the JAVA_OPTS from environment
#ENTRYPOINT ["java","--enable-preview","org.springframework.boot.loader.JarLauncher"]
