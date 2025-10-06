FROM eclipse-temurin:21-jdk-alpine
WORKDIR /cash
COPY build/libs/*.jar cash.jar
ENTRYPOINT ["java", "-jar", "cash.jar"]