# First Stage: Build with Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the pom.xml first to cache dependencies
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

# Now copy the rest of the project files and build the project
COPY . .
RUN --mount=type=cache,target=/root/.m2 mvn clean package

# Second Stage: Create a minimal image with only the JAR file
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/openapi-spring-1.0.0.jar /app/openapi-spring-1.0.0.jar

# Run the application
CMD ["java", "-jar", "openapi-spring-1.0.0.jar"]
