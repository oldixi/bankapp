FROM eclipse-temurin:21-jdk-alpine
WORKDIR /accounts
COPY accounts-serv/build/libs/*.jar accounts.jar
ENTRYPOINT ["java", "-jar", "accounts.jar"]