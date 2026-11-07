# syntax=docker/dockerfile:1
#
# Salve como backend/Dockerfile
# Java 25 (LTS): o pom.xml precisa declarar <java.version>25</java.version>
 
# ---- Build ----
FROM maven:3.9-eclipse-temurin-25 AS builder
WORKDIR /app
COPY . .
# Cache de dependências do Maven entre builds (BuildKit)
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package
 
# ---- Runtime (JRE, sem ferramentas de build) ----
FROM eclipse-temurin:25-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
USER app
EXPOSE 8080
# Memória e fuso da JVM vêm de JAVA_TOOL_OPTIONS (docker-compose.yml)
ENTRYPOINT ["java", "-jar", "app.jar"]