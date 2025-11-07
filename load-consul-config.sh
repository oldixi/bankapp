# common
consul kv put config/apps/logging.level.root "info"
consul kv put config/apps/logging.level.org.springframework.cloud.consul "info"

consul kv put config/apps/feign.circuitbreaker.enabled "true"
consul kv put config/apps/feign.client.config.default.connectTimeout "5000"
consul kv put config/apps/feign.client.config.default.readTimeout "5000"
consul kv put config/apps/feign.config.default.retryer "feign.Retryer.Default"

consul kv put config/apps/spring.security.oauth2.client.provider.keycloak.issuer-uri "http://localhost:8080/realms/bankapp"
consul kv put config/apps/spring.security.oauth2.client.registration.keycloak.provider "keycloak"
consul kv put config/apps/spring.security.oauth2.client.registration.keycloak.authorization-grant-type "client_credentials"
consul kv put config/apps/spring.security.oauth2.client.registration.keycloak.scope "openid,profile,email"
consul kv put config/apps/spring.security.oauth2.client.registration.keycloak.clientAuthenticationMethod "client_secret_basic"
consul kv put config/apps/spring.security.oauth2.client.registration.keycloak.issuer-uri "http://localhost:8080/realms/bankapp"

consul kv put config/apps/spring.security.oauth2.resourceserver.jwt.issuer-uri "http://localhost:8080/realms/bankapp"
consul kv put config/apps/spring.security.oauth2.resourceserver.jwt.jwk-set-uri "http://localhost:8080/realms/bankapp/protocol/openid-connect/certs"
consul kv put config/apps/client.registration.id "keycloak"

consul kv put config/apps/spring.cloud.openfeign.oauth2.enabled "true"
consul kv put config/apps/spring.cloud.openfeign.oauth2.clientRegistrationId "keycloak"

# accounts
consul kv put config/accounts/server.port "8082" 
consul kv put config/accounts/spring.application.name "accounts"

consul kv put config/accounts/spring.datasource.url "jdbc:postgresql://localhost:5432/bankapp"
consul kv put config/accounts/spring.datasource.username "bankapp"
consul kv put config/accounts/spring.datasource.password "bankapp"
consul kv put config/accounts/spring.datasource.driver-class-name "org.postgresql.Driver"

consul kv put config/accounts/spring.liquibase.url "jdbc:postgresql://localhost:5432/bankapp"
consul kv put config/accounts/spring.liquibase.user "bankapp"
consul kv put config/accounts/spring.liquibase.password "bankapp"

consul kv put config/accounts/spring.security.oauth2.client.registration.keycloak.client-id "accounts"
consul kv put config/accounts/spring.security.oauth2.client.registration.keycloak.client-secret "vRBqHqy5rIgQjqHLk4ntrFMflfZZ1V5Y"

consul kv put config/accounts/resilience4j.circuitbreaker.instances.notifications.register-health-indicator "true"
consul kv put config/accounts/resilience4j.circuitbreaker.instances.notifications.failure-rate-threshold "50"
consul kv put config/accounts/resilience4j.circuitbreaker.instances.notifications.record-exceptions[0] "feign.RetryableException"
consul kv put config/accounts/resilience4j.circuitbreaker.instances.notifications.record-exceptions[1] "java.net.UnknownHostException"
consul kv put config/accounts/resilience4j.circuitbreaker.instances.notifications.record-exceptions[2] "org.springframework.web.client.ResourceAccessException"
consul kv put config/accounts/resilience4j.circuitbreaker.instances.notifications.record-exceptions[3] "java.io.IOException"


# cash
consul kv put config/cash/server.port "8083" 
consul kv put config/cash/spring.application.name "cash"

consul kv put config/cash/spring.security.oauth2.client.registration.keycloak.client-id "cash"
consul kv put config/cash/spring.security.oauth2.client.registration.keycloak.client-secret "VKgaEyKXFsc5QJJrtDolB2Luv7KyeXth"
consul kv put config/cash/resilience4j.circuitbreaker.instances.accounts.register-health-indicator "true"
consul kv put config/cash/resilience4j.circuitbreaker.instances.accounts.failure-rate-threshold "50"
consul kv put config/cash/resilience4j.circuitbreaker.instances.accounts.record-exceptions[0] "feign.RetryableException"
consul kv put config/cash/resilience4j.circuitbreaker.instances.accounts.record-exceptions[1] "java.net.UnknownHostException"
consul kv put config/cash/resilience4j.circuitbreaker.instances.accounts.record-exceptions[2] "org.springframework.web.client.ResourceAccessException"
consul kv put config/cash/resilience4j.circuitbreaker.instances.accounts.record-exceptions[3] "java.io.IOException"


# transfer
consul kv put config/transfer/server.port "8084" 
consul kv put config/transfer/spring.application.name "transfer"

consul kv put config/transfer/spring.security.oauth2.client.registration.keycloak.client-id "transfer"
consul kv put config/transfer/spring.security.oauth2.client.registration.keycloak.client-secret "tFVIAzOu86RAkgbIzmZgEkeCoOYk74w1"
consul kv put config/transfer/resilience4j.circuitbreaker.instances.accounts.register-health-indicator "true"
consul kv put config/transfer/resilience4j.circuitbreaker.instances.accounts.failure-rate-threshold "50"
consul kv put config/transfer/resilience4j.circuitbreaker.instances.accounts.record-exceptions[0] "feign.RetryableException"
consul kv put config/transfer/resilience4j.circuitbreaker.instances.accounts.record-exceptions[1] "java.net.UnknownHostException"
consul kv put config/transfer/resilience4j.circuitbreaker.instances.accounts.record-exceptions[2] "org.springframework.web.client.ResourceAccessException"
consul kv put config/transfer/resilience4j.circuitbreaker.instances.accounts.record-exceptions[3] "java.io.IOException"


# notifications
consul kv put config/notifications/server.port "8085" 
consul kv put config/notifications/spring.application.name "notifications"

consul kv put config/notifications/spring.security.oauth2.client.registration.keycloak.client-id "notifications"
consul kv put config/notifications/spring.security.oauth2.client.registration.keycloak.client-secret "zwNU03EpVjSvqo7UpsJdghw6v0EVe0hC"


# front
consul kv put config/front/server.port "8086" 
consul kv put config/front/spring.application.name "front"

consul kv put config/front/spring.security.oauth2.client.registration.keycloak.client-id "front"
consul kv put config/front/spring.security.oauth2.client.registration.keycloak.client-secret "moY8OTX4GbDI5AwmholMgAXT0aJDCSpf"

consul kv put config/front/resilience4j.circuitbreaker.instances.accounts.register-health-indicator "true"
consul kv put config/front/resilience4j.circuitbreaker.instances.accounts.failure-rate-threshold "50"

consul kv put config/front/resilience4j.circuitbreaker.instances.cash.register-health-indicator "true"
consul kv put config/front/resilience4j.circuitbreaker.instances.cash.failure-rate-threshold "50"

consul kv put config/front/resilience4j.circuitbreaker.instances.transfer.register-health-indicator "true"
consul kv put config/front/resilience4j.circuitbreaker.instances.transfer.failure-rate-threshold "50"


# gateway
consul kv put config/gateway/server.port "8087"
consul kv put config/gateway/spring.application.name "gateway"
consul kv put config/gateway/spring.cloud.gateway.discovery.locator.enabled "true"

consul kv put config/gateway/spring.security.oauth2.client.registration.keycloak.client-id "gateway"
consul kv put config/gateway/spring.security.oauth2.client.registration.keycloak.client-secret "sX48hILCYvgMy1f7Cql6RVw5TA4Xpxzh"