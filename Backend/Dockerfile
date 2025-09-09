# ================================
# Stage 1: Build the JAR
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working directory inside container
WORKDIR /app

# Copy pom.xml and download dependencies (layer caching)
COPY pom.xml .
COPY src ./src

# Package the application (skip tests for faster builds)
RUN mvn clean package -DskipTests

# ================================
# Stage 2: Run the JAR for multi stage conatainer. 
# ================================
FROM openjdk:21-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/Backend-0.0.1-SNAPSHOT.jar app.jar

# Expose application port (default for Spring Boot)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
