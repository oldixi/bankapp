FROM eclipse-temurin:21-jre
WORKDIR /app

COPY front/build/libs/front.jar front.jar

ENTRYPOINT ["java", "-jar", "front.jar"]