FROM maven:3.10.0-eclipse-temurin-27 AS builder

WORKDIR /app

COPY . .

RUN mvn -B clean package -Dmaven.test.skip=true

FROM eclipse-temurin:27-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]