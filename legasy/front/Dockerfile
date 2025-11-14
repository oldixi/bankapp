FROM eclipse-temurin:21-jdk-alpine
WORKDIR /front
COPY ../../front/build/libs ./
RUN find . -name "front-*.jar" ! -name "*plain*" ! -name "*stubs*" -exec cp {} front.jar \;
ENTRYPOINT ["java", "-jar", "front.jar"]