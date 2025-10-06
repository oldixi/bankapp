FROM eclipse-temurin:21-jdk-alpine
WORKDIR /notifications
COPY build/libs/*.jar notifications.jar
ENTRYPOINT ["java", "-jar", "notifications.jar"]