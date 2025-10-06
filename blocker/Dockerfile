FROM eclipse-temurin:21-jdk-alpine
WORKDIR /blocker
COPY build/libs/*.jar blocker.jar
ENTRYPOINT ["java", "-jar", "blocker.jar"]