FROM eclipse-temurin:21-jre
WORKDIR /app

COPY notifications/build/libs/notifications.jar notifications.jar

ENTRYPOINT ["java", "-jar", "notifications.jar"]