FROM maven:3.9.9-eclipse-temurin-8 AS build

WORKDIR /app

COPY pom.xml settings.xml* ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:8-jre-jammy AS runtime

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/eSimulate-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=6 \
    CMD curl --fail --silent http://localhost:8080/api/actuator/health > /dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
