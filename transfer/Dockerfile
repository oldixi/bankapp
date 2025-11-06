FROM eclipse-temurin:21-jdk-alpine
WORKDIR /transfer
COPY build/libs/ ./
RUN find . -name "transfer-*.jar" ! -name "*plain*" ! -name "*stubs*" -exec cp {} transfer.jar \;
ENTRYPOINT ["java", "-jar", "transfer.jar"]