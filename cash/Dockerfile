FROM eclipse-temurin:21-jre
WORKDIR /app

COPY cash/build/libs/cash.jar cash.jar

ENTRYPOINT ["java", "-jar", "cash.jar"]