FROM gradle:8.7-jdk21 AS build
WORKDIR /workspace

COPY ../build.gradle .
COPY ../settings.gradle .
COPY ../gradlew .
COPY ../gradle/wrapper/gradle-wrapper.jar ../gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/

COPY src ./src

RUN gradle :front:build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/front/build/libs/front.jar front.jar

ENTRYPOINT ["java", "-jar", "front.jar"]