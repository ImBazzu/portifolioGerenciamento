# Etapa 1: Build com Maven + Java 25
FROM maven:3.9.16-eclipse-temurin-25 AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]