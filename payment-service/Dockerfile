# Use official OpenJDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory in container
WORKDIR /app

# Copy the jar file (make sure you build it first with Maven)
COPY target/paymentservice-0.0.1-SNAPSHOT.jar app.jar

# Expose the default port your Spring Boot app runs on
EXPOSE 8083

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
