FROM eclipse-temurin:21-jdk-alpine
WORKDIR /exchange-generator
COPY build/libs/*.jar exchange-generator.jar
ENTRYPOINT ["java", "-jar", "exchange-generator.jar"]