# Build Stage
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app

# Kopiere pom.xml und dependencies first (für caching)
COPY pom.xml .
COPY service-apprunner/pom.xml service-apprunner/pom.xml
COPY library/pom.xml library/pom.xml
COPY validator/pom.xml validator/pom.xml
COPY Mustang-CLI/pom.xml Mustang-CLI/pom.xml

# Skip dependency resolution for local dependencies - they'll be built as needed

# Kopiere Source Code
COPY library/src library/src
COPY validator/src validator/src
COPY Mustang-CLI/src Mustang-CLI/src
COPY service-apprunner/src service-apprunner/src

# Package die Application - build in dependency order
RUN mvn clean install -DskipTests -pl library
RUN mvn clean install -DskipTests -pl validator -am
RUN mvn clean package -DskipTests -pl service-apprunner -am

# Runtime Stage
FROM openjdk:11-jre-slim

WORKDIR /app

# Install curl für Health Checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Kopiere das gebaute JAR
COPY --from=build /app/service-apprunner/target/*.jar app.jar

# Health Check für AWS App Runner wurde entfernt
# Löst Fehler aus, da Health Check ausgeführt wird, bevor der Service gestartet ist

# Expose Port
EXPOSE 8080

# Run als non-root user
RUN useradd -m myuser
USER myuser

# Entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]