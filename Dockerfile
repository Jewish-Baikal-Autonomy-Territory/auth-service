FROM maven:3.9-eclipse-temurin-21 AS builder
LABEL authors="ha1t0"
WORKDIR /app

COPY pom.xml .
COPY proto/pom.xml proto/
COPY auth/pom.xml auth/

RUN mvn dependency:go-offline -B

COPY . .

RUN mvn clean package -DskipTests -pl auth -am

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/auth/target/auth-*.jar app.jar

EXPOSE ${SERVER_PORT}
EXPOSE 9090

HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD curl -f http://localhost:${SERVER_PORT}/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]