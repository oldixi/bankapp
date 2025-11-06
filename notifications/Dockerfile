FROM eclipse-temurin:21-jdk-alpine
WORKDIR /notifications
COPY build/libs/ ./
RUN find . -name "notifications-*.jar" ! -name "*plain*" ! -name "*stubs*" -exec cp {} notifications.jar \;
ENTRYPOINT ["java", "-jar", "notifications.jar"]