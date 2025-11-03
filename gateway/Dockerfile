FROM eclipse-temurin:21-jdk-alpine
WORKDIR /gateway
COPY build/libs/*.jar gateway.jar
ENTRYPOINT ["java", "-jar", "gateway.jar"]