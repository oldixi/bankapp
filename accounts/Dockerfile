FROM gradle:8.7-jdk21 AS build
WORKDIR /workspace

COPY ../build.gradle .
COPY ../settings.gradle .
COPY ../gradlew .
COPY ../gradle/wrapper/gradle-wrapper.jar ../gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/

COPY accounts-serv/src ./src

RUN gradle :accounts-serv:build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/accounts-serv/build/libs/accounts.jar accounts.jar

ENTRYPOINT ["java", "-jar", "accounts.jar"]