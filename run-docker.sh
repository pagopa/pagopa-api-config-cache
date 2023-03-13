

docker rm -f cache
docker run \
--name cache \
--env DB_CONFIG_URL="jdbc:postgresql://pagopa-d-weu-nodo-flexible-postgresql.postgres.database.azure.com:6432/nodo?sslmode=require&prepareThreshold=0&currentSchema=cfg" \
--env DB_CONFIG_USER="cfg"          \
--env REDIS_HOST="redis"            \
--env REDIS_PORT="6379"             \
--env DB_CONFIG_PASSWORD="password" \
-v "$(pwd)/helm/config/dev/application.properties:/tmp/application.properties" \
--env JAVA_OPTS='-Xmx2048m -Dspring.config.location=/tmp/application.properties' \
-p 8080:8080 \
cache:0.0.1
