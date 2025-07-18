# Build stage
FROM maven:3.9.11-sapmachine-24 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Runtime stage
FROM openjdk:24-jdk-slim-bullseye
WORKDIR /app
COPY --from=build /app/target/cake-manager-*.jar app.jar

# Create a non-root user
RUN useradd -r -s /bin/false appuser
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
