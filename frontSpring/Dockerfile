FROM maven:3.10.0-eclipse-temurin-27 AS builder

WORKDIR /app

COPY . .

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:27-jdk-alpine

WORKDIR /app

COPY --from=builder /app/target/frontSpring-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]
