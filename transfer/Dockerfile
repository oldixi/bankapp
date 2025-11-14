FROM eclipse-temurin:21-jre
WORKDIR /app

COPY transfer/build/libs/transfer.jar transfer.jar

ENTRYPOINT ["java", "-jar", "transfer.jar"]