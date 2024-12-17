# Stage 1: Build with Maven
FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the Maven file for workers
COPY ./workers/pom.xml ./pom.xml

# Copy the 'src' directory from 'out' folder
COPY ./out/src ./src

# Cache Maven dependencies
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

# Build the application
RUN --mount=type=cache,target=/root/.m2 mvn clean package

# Stage 2: Run the application with Tesseract
FROM openjdk:17-jdk-slim

# Install Tesseract and required libraries
RUN apt-get update && \
    apt-get install -y tesseract-ocr libtesseract-dev && \
    apt-get clean

# Copy tessdata files from local context
COPY ./workers/tessdata /usr/share/tesseract-ocr/4.00/tessdata

# Set up application directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/workers-service-1.0.0.jar ./app.jar

# Run the application
CMD ["java", "-jar", "app.jar"]
