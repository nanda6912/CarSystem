# Smart Parking System - Production Dockerfile
FROM openjdk:21-jre-slim

# Set working directory
WORKDIR /app

# Copy application JAR
COPY target/*.jar app.jar

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser
RUN chown -R appuser:appuser /app
USER appuser

# Expose application port
EXPOSE 8085

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

# Application entrypoint
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Add labels for metadata
LABEL maintainer="Smart Parking Team" \
      version="7.0.0" \
      description="Smart Parking Management System" \
      org.opencontainers.image.source="https://github.com/nanda6912/CarSystem"
