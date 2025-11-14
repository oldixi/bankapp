FROM eclipse-temurin:21-jdk-alpine
WORKDIR /accounts
COPY ../../accounts/accounts-serv/build/libs ./
RUN find . -name "accounts-*.jar" ! -name "*plain*" ! -name "*stubs*" -exec mv {} accounts.jar \;
ENTRYPOINT ["java", "-jar", "accounts.jar"]