FROM eclipse-temurin:21-jdk-alpine
WORKDIR /cash
COPY build/libs/ ./
RUN find . -name "cash-*.jar" ! -name "*plain*" ! -name "*stubs*" -exec cp {} cash.jar \;
ENTRYPOINT ["java", "-jar", "cash.jar"]