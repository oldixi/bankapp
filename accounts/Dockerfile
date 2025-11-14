FROM eclipse-temurin:21-jre
WORKDIR /app

COPY accounts/accounts-serv/build/libs/accounts.jar accounts.jar

ENTRYPOINT ["java", "-jar", "accounts.jar"]