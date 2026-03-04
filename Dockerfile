# =============================================================================
# Smart Parking System - Production Dockerfile
# =============================================================================
# Multi-stage build for optimal size and compatibility
# =============================================================================

# Build Stage
FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine

# Install necessary packages
RUN apk update && \
    apk add --no-cache curl && \
    rm -rf /var/cache/apk/*

# Create application user
RUN addgroup -g 1000 appuser && \
    adduser -D -s /bin/sh -u 1000 -G appuser appuser

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to appuser
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8085

# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8085/actuator/health || exit 1

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Application entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels for metadata
LABEL maintainer="Smart Parking Team"
LABEL version="7.0.0"
LABEL description="Smart Parking Management System"
LABEL org.opencontainers.image.source="https://github.com/nanda6912/CarSystem"
