# First Stage: Build with Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml from the 'out' directory
COPY out/pom.xml ./

# Copy the source code from the 'out/src' directory
COPY out/src ./src

# Cache Maven dependencies
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

# Build the project
RUN --mount=type=cache,target=/root/.m2 mvn clean package

# Second Stage: Create a minimal image
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/openapi-spring-1.0.0.jar /app/openapi-spring-1.0.0.jar

# Run the application
CMD ["java", "-jar", "openapi-spring-1.0.0.jar"]
