# Etapa 1: Build com Maven + Java 27
FROM maven:3.10.0-eclipse-temurin-27 AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:27-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]