FROM eclipse-temurin:21-jdk-alpine
WORKDIR /gateway
COPY build/libs/ ./
RUN find . -name "gateway-*.jar" ! -name "*plain*" ! -name "*stubs*" -exec cp {} gateway.jar \;
ENTRYPOINT ["java", "-jar", "gateway.jar"]