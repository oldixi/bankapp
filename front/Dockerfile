FROM eclipse-temurin:21-jdk-alpine
WORKDIR /front
COPY build/libs/*.jar front.jar
ENTRYPOINT ["java", "-jar", "front.jar"]