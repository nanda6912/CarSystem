# Smart Parking System - Production Docker Image
FROM openjdk:21-jre-slim

# Set working directory
WORKDIR /app

# Create non-root user for security
RUN groupadd -r parking && useradd -r -g parking parking

# Install required packages
RUN apt-get update && \
    apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy application JAR
COPY target/smart-parking-system-1.0.0.jar app.jar

# Change ownership to non-root user
RUN chown -R parking:parking /app

# Switch to non-root user
USER parking

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8085/api/slots || exit 1

# Expose port
EXPOSE 8085

# JVM optimizations for production
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
