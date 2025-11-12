FROM gradle:8.7-jdk21 AS build
WORKDIR /workspace

COPY ../build.gradle .
COPY ../settings.gradle .
COPY ../gradlew .
COPY ../gradle/wrapper/gradle-wrapper.jar ../gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/

COPY src ./src

RUN gradle :cash:build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/cash/build/libs/cash.jar cash.jar

ENTRYPOINT ["java", "-jar", "cash.jar"]