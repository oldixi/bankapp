#!/bin/bash

# This script loads configuration into Consul for Docker environment

put_kv() {
    local key="$1"
    local value="$2"
    echo "Putting $key=$value"
    curl -s -X PUT -d "$value" "http://$CONSUL_HOST:$CONSUL_PORT/v1/kv/$key" > /dev/null
}

wait_for_consul() {
    echo "Waiting for Consul to be ready at $CONSUL_HOST:$CONSUL_PORT"
    for i in {1..30}; do
        if curl -s "http://$CONSUL_HOST:$CONSUL_PORT/v1/status/leader" > /dev/null; then
            echo "Consul is ready"
            return 0
        fi
        echo "Waiting for Consul... ($i/30)"
        sleep 2
    done
    echo "Consul failed to start"
    exit 1
}

load_configuration() {
    echo "Loading configuration into Consul"

    # Common configuration
    echo "Loading common configuration"
    put_kv "config/apps/logging.level.root" "info"
    put_kv "config/apps/logging.level.org.springframework.cloud.consul" "info"

    put_kv "config/apps/feign.circuitbreaker.enabled" "true"
    put_kv "config/apps/feign.client.config.default.connectTimeout" "5000"
    put_kv "config/apps/feign.client.config.default.readTimeout" "5000"
    put_kv "config/apps/feign.config.default.retryer" "feign.Retryer.Default"

    put_kv "config/apps/spring.security.oauth2.client.provider.keycloak.issuer-uri" "http://keycloak:8080/realms/bankapp"
    put_kv "config/apps/spring.security.oauth2.client.registration.keycloak.provider" "keycloak"
    put_kv "config/apps/spring.security.oauth2.client.registration.keycloak.authorization-grant-type" "client_credentials"
    put_kv "config/apps/spring.security.oauth2.client.registration.keycloak.scope" "openid,profile,email"
    put_kv "config/apps/spring.security.oauth2.client.registration.keycloak.clientAuthenticationMethod" "client_secret_basic"
    put_kv "config/apps/spring.security.oauth2.client.registration.keycloak.issuer-uri" "http://keycloak:8080/realms/bankapp"

    put_kv "config/apps/spring.security.oauth2.resourceserver.jwt.issuer-uri" "http://keycloak:8080/realms/bankapp"
    put_kv "config/apps/spring.security.oauth2.resourceserver.jwt.jwk-set-uri" "http://keycloak:8080/realms/bankapp/protocol/openid-connect/certs"
    put_kv "config/apps/client.registration.id" "keycloak"

    put_kv "config/apps/spring.cloud.openfeign.oauth2.enabled" "true"
    put_kv "config/apps/spring.cloud.openfeign.oauth2.clientRegistrationId" "keycloak"

    # Accounts service
    echo "Loading accounts service configuration"
    put_kv "config/accounts/server.port" "8082"
    put_kv "config/accounts/spring.application.name" "accounts"

    put_kv "config/accounts/spring.datasource.url" "jdbc:postgresql://db:5432/bankapp"
    put_kv "config/accounts/spring.datasource.username" "bankapp"
    put_kv "config/accounts/spring.datasource.password" "bankapp"
    put_kv "config/accounts/spring.datasource.driver-class-name" "org.postgresql.Driver"

    put_kv "config/accounts/spring.liquibase.url" "jdbc:postgresql://db:5432/bankapp"
    put_kv "config/accounts/spring.liquibase.user" "bankapp"
    put_kv "config/accounts/spring.liquibase.password" "bankapp"

    put_kv "config/accounts/spring.security.oauth2.client.registration.keycloak.client-id" "accounts"
    put_kv "config/accounts/spring.security.oauth2.client.registration.keycloak.client-secret" "vRBqHqy5rIgQjqHLk4ntrFMflfZZ1V5Y"

    put_kv "config/accounts/resilience4j.circuitbreaker.instances.notifications.register-health-indicator" "true"
    put_kv "config/accounts/resilience4j.circuitbreaker.instances.notifications.failure-rate-threshold" "50"

    # Cash service
    echo "Loading cash service configuration"
    put_kv "config/cash/server.port" "8083"
    put_kv "config/cash/spring.application.name" "cash"

    put_kv "config/cash/spring.security.oauth2.client.registration.keycloak.client-id" "cash"
    put_kv "config/cash/spring.security.oauth2.client.registration.keycloak.client-secret" "VKgaEyKXFsc5QJJrtDolB2Luv7KyeXth"

    put_kv "config/cash/resilience4j.circuitbreaker.instances.accounts.register-health-indicator" "true"
    put_kv "config/cash/resilience4j.circuitbreaker.instances.accounts.failure-rate-threshold" "50"

    # Transfer service
    echo "Loading transfer service configuration"
    put_kv "config/transfer/server.port" "8084"
    put_kv "config/transfer/spring.application.name" "transfer"

    put_kv "config/transfer/spring.security.oauth2.client.registration.keycloak.client-id" "transfer"
    put_kv "config/transfer/spring.security.oauth2.client.registration.keycloak.client-secret" "tFVIAzOu86RAkgbIzmZgEkeCoOYk74w1"

    put_kv "config/transfer/resilience4j.circuitbreaker.instances.accounts.register-health-indicator" "true"
    put_kv "config/transfer/resilience4j.circuitbreaker.instances.accounts.failure-rate-threshold" "50"

    # Notifications service
    echo "Loading notifications service configuration"
    put_kv "config/notifications/server.port" "8085"
    put_kv "config/notifications/spring.application.name" "notifications"

    put_kv "config/notifications/spring.security.oauth2.client.registration.keycloak.client-id" "notifications"
    put_kv "config/notifications/spring.security.oauth2.client.registration.keycloak.client-secret" "zwNU03EpVjSvqo7UpsJdghw6v0EVe0hC"

    # Front service
    echo "Loading front service configuration"
    put_kv "config/front/server.port" "8086"
    put_kv "config/front/spring.application.name" "front"

    put_kv "config/front/spring.security.oauth2.client.registration.keycloak.client-id" "front"
    put_kv "config/front/spring.security.oauth2.client.registration.keycloak.client-secret" "moY8OTX4GbDI5AwmholMgAXT0aJDCSpf"

    put_kv "config/front/resilience4j.circuitbreaker.instances.accounts.register-health-indicator" "true"
    put_kv "config/front/resilience4j.circuitbreaker.instances.accounts.failure-rate-threshold" "50"

    put_kv "config/front/resilience4j.circuitbreaker.instances.cash.register-health-indicator" "true"
    put_kv "config/front/resilience4j.circuitbreaker.instances.cash.failure-rate-threshold" "50"

    put_kv "config/front/resilience4j.circuitbreaker.instances.transfer.register-health-indicator" "true"
    put_kv "config/front/resilience4j.circuitbreaker.instances.transfer.failure-rate-threshold" "50"

    # Gateway service
    echo "Loading gateway service configuration"
    put_kv "config/gateway/server.port" "8087"
    put_kv "config/gateway/spring.application.name" "gateway"
    put_kv "config/gateway/spring.cloud.gateway.discovery.locator.enabled" "true"

    put_kv "config/gateway/spring.security.oauth2.client.registration.keycloak.client-id" "gateway"
    put_kv "config/gateway/spring.security.oauth2.client.registration.keycloak.client-secret" "sX48hILCYvgMy1f7Cql6RVw5TA4Xpxzh"

    # test-profile
    put_kv "config/apps-test/spring.security.oauth2.client.provider.keycloak.issuer-uri" "http://localhost:8081/realms/bankapp"
    put_kv "config/apps-test/spring.security.oauth2.client.registration.keycloak.issuer-uri" "http://localhost:8081/realms/bankapp"
    put_kv "config/apps-test/spring.security.oauth2.resourceserver.jwt.issuer-uri" "http://localhost:8081/realms/bankapp"
    put_kv "config/apps-test/spring.security.oauth2.resourceserver.jwt.jwk-set-uri" "http://localhost:8081/realms/bankapp/protocol/openid-connect/certs"
    put_kv "config/accounts-test/spring.datasource.url" "jdbc:postgresql://localhost:5432/bankapp"
    put_kv "config/accounts-test/spring.liquibase.url" "jdbc:postgresql://localhost:5432/bankapp"
}

main() {
    echo "BankApp Consul Configuration Loader"
    wait_for_consul
    load_configuration
}

main "$@"