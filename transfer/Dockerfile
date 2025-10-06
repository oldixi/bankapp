FROM eclipse-temurin:21-jdk-alpine
WORKDIR /transfer
COPY build/libs/*.jar transfer.jar
ENTRYPOINT ["java", "-jar", "transfer.jar"]