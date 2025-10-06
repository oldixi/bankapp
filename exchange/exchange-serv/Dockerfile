FROM eclipse-temurin:21-jdk-alpine
WORKDIR /exchange
COPY build/libs/*.jar exchange.jar
ENTRYPOINT ["java", "-jar", "exchange.jar"]