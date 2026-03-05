# 🚀 Smart Parking System - Production Deployment Guide v7.0

## 📋 **Table of Contents**
1. [Prerequisites](#prerequisites)
2. [Quick Start Options](#quick-start-options)
3. [Local Development Setup](#local-development-setup)
4. [Production Deployment](#production-deployment)
5. [Database Setup](#database-setup)
6. [Docker Deployment](#docker-deployment)
7. [Cloud Deployment](#cloud-deployment)
8. [Testing & Verification](#testing--verification)
9. [Monitoring & Maintenance](#monitoring--maintenance)
10. [Troubleshooting](#troubleshooting)

---

## **🔧 Prerequisites**

### **System Requirements**
- **Java 21+** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **PostgreSQL 18.2+** (for production)
- **Git** (for version control)

### **Optional Requirements**
- **Docker & Docker Compose** (for containerized deployment)
- **Node.js 16+** (for development tools)
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)
- **Nginx** (for reverse proxy)

---

## **⚡ Quick Start Options**

### **🐳 Docker Deployment (Recommended - 2 minutes)**
```bash
# Clone and run with Docker
git clone https://github.com/nanda6912/CarSystem.git
cd CarSystem
docker-compose up -d

# Access: http://localhost:8085
# Database: localhost:5432
```

### **🏃‍♂️ Quick Local Setup (5 minutes)**
```bash
# Clone repository
git clone https://github.com/nanda6912/CarSystem.git
cd CarSystem

# Start PostgreSQL
net start postgresql-x64-18

# Setup database
$env:PGPASSWORD="Nanda@123"; psql -U postgres -c "CREATE DATABASE smart_parking_db;"
$env:PGPASSWORD="Nanda@123"; psql -U postgres -d smart_parking_db -f src/main/resources/schema.sql

# Start application
mvn spring-boot:run

# Access: http://localhost:8085
```

---

## **💻 Local Development Setup**

### **Step 1: Clone Repository**
```bash
git clone https://github.com/nanda6912/CarSystem.git
cd CarSystem
```

### **Step 2: Database Setup (PostgreSQL)**
```bash
# Start PostgreSQL Service
net start postgresql-x64-18  # Windows
# OR
sudo systemctl start postgresql  # Linux

# Create Database with password
$env:PGPASSWORD="Nanda@123"; psql -U postgres -c "CREATE DATABASE smart_parking_db;"

# Initialize Schema (200 slots)
$env:PGPASSWORD="Nanda@123"; psql -U postgres -d smart_parking_db -f src/main/resources/schema.sql
```

### **Step 3: Configuration Verification**
```bash
# Verify application.properties contains:
# Database URL: jdbc:postgresql://localhost:5432/smart_parking_db
# Username: postgres
# Password: Nanda@123
# Server Port: 8085
```

### **Step 4: Start Application**
```bash
# Build and Run
mvn clean spring-boot:run

# Application will be available at:
# All Applications: http://localhost:8085
# Backend API: http://localhost:8085/api/
# Frontend Apps: http://localhost:8085/*.html
```

---

## **🌐 Production Deployment Options**

### **🐳 Option 1: Docker Deployment (Recommended)**

#### **Complete System Setup**
```bash
# Clone repository
git clone https://github.com/nanda6912/CarSystem.git
cd CarSystem

# Build and start all services
docker-compose up -d

# Services included:
# - PostgreSQL 18.2 database
# - Redis caching (optional)
# - Smart Parking Application
# - Nginx reverse proxy

# Access: http://localhost:8085
# Database: localhost:5432
```

#### **Docker Services**
- **parking-db**: PostgreSQL 18.2 with automatic schema initialization
- **parking-redis**: Redis for session caching (optional)
- **parking-app**: Main Spring Boot application
- **parking-nginx**: Reverse proxy with SSL termination

### **🏢 Option 2: Traditional Server Deployment**

#### **Step 1: Prepare Server**
```bash
# Install Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# Install PostgreSQL 18
sudo apt install postgresql-18 postgresql-contrib

# Install Maven
sudo apt install maven
```

#### **Step 2: Setup Database**
```bash
# Switch to postgres user
sudo -i -u postgres

# Create database and user
createdb smart_parking_db
createuser --interactive parking_user

# Grant privileges
psql -d smart_parking_db -c "GRANT ALL PRIVILEGES ON DATABASE smart_parking_db TO parking_user;"
```

#### **Step 3: Deploy Application**
```bash
# Clone repository
git clone https://github.com/nanda6912/CarSystem.git
cd CarSystem

# Update production configuration
cp src/main/resources/application-prod.properties src/main/resources/application.properties

# Build application
mvn clean package -DskipTests

# Run application
java -jar target/smart-parking-system-1.0.0.jar
```

### **Option 2: Docker Deployment**

#### **Step 1: Create Dockerfile**
```dockerfile
FROM openjdk:21-jdk-slim

WORKDIR /app
COPY target/smart-parking-system-1.0.0.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### **Step 2: Build and Run**
```bash
# Build Docker image
docker build -t smart-parking-system .

# Run with PostgreSQL
docker run -d \
  --name parking-db \
  -e POSTGRES_DB=smart_parking_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=Nanda@123 \
  -p 5432:5432 \
  postgres:18

# Run application
docker run -d \
  --name parking-app \
  --link parking-db:postgres \
  -p 8085:8085 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/smart_parking_db \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=Nanda@123 \
  smart-parking-system
```

### **Option 3: Cloud Deployment (Render)**

#### **Step 1: Prepare for Render**
```bash
# Update application-render.properties with your database URL
# Render will automatically set DATABASE_URL environment variable
```

#### **Step 2: Deploy to Render**
1. Connect GitHub repository to Render
2. Create Web Service
3. Set Build Command: `mvn clean package`
4. Set Start Command: `java -jar target/smart-parking-system-1.0.0.jar`
5. Add PostgreSQL database add-on
6. Deploy!

---

## **🗄️ Database Setup**

### **PostgreSQL 18.2 Production Setup**

#### **Configuration Optimization**
```sql
-- postgresql.conf optimizations
shared_buffers = 256MB
effective_cache_size = 1GB
maintenance_work_mem = 64MB
checkpoint_completion_target = 0.9
wal_buffers = 16MB
default_statistics_target = 100
random_page_cost = 1.1
effective_io_concurrency = 200
```

#### **Security Setup**
```sql
-- Create dedicated user
CREATE USER parking_user WITH PASSWORD 'secure_password';

-- Grant permissions
GRANT CONNECT ON DATABASE smart_parking_db TO parking_user;
GRANT USAGE ON SCHEMA public TO parking_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO parking_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO parking_user;
```

#### **Backup Strategy**
```bash
# Daily backup
pg_dump -h localhost -U postgres smart_parking_db > backup_$(date +%Y%m%d).sql

# Automated backup script
#!/bin/bash
BACKUP_DIR="/var/backups/parking"
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -h localhost -U postgres smart_parking_db > $BACKUP_DIR/backup_$DATE.sql
find $BACKUP_DIR -name "backup_*.sql" -mtime +7 -delete
```

---

## **🧪 Testing & Verification**

### **API Testing**
```bash
# Test health endpoint
curl http://localhost:8085/api/slots

# Test booking creation
curl -X POST http://localhost:8085/api/bookings \
  -H "Content-Type: application/json" \
  -d '{"slotId":"GA001","vehicleNumber":"TEST123","phoneNumber":"1234567890"}'

# Test static files
curl -I http://localhost:8085/
curl -I http://localhost:8085/public-booking.html
```

### **Frontend Testing**
```bash
# Access all applications
http://localhost:8085/
http://localhost:8085/public-booking.html
http://localhost:8085/professional-dashboard.html
http://localhost:8085/professional-entry-terminal.html
http://localhost:8085/professional-exit-terminal.html
http://localhost:8085/auth.html
```

### **Database Testing**
```sql
-- Verify slots
SELECT COUNT(*) FROM parking_slots;
SELECT * FROM parking_slots LIMIT 5;

-- Verify bookings
SELECT COUNT(*) FROM bookings;
SELECT * FROM bookings ORDER BY entry_time DESC LIMIT 5;
```

### **Performance Testing**
```bash
# Load test with Apache Bench
ab -n 1000 -c 10 http://localhost:8085/api/slots

# Memory usage monitoring
jstat -gc [PID] 250ms

# Database performance
psql -d smart_parking_db -c "SELECT * FROM pg_stat_activity;"
```

---

## **🔧 Troubleshooting**

### **Common Issues**

#### **Application Won't Start**
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check port availability
netstat -tulpn | grep :8085

# View logs
tail -f logs/application.log
```

#### **Database Connection Issues**
```bash
# Test PostgreSQL connection
psql -h localhost -U postgres -d smart_parking_db

# Check PostgreSQL status
sudo systemctl status postgresql

# View PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-18-main.log
```

#### **Frontend Not Loading**
```bash
# Check static files
ls -la src/main/resources/static/

# Check CORS configuration
curl -H "Origin: http://localhost:8085" -H "Access-Control-Request-Method: GET" -X OPTIONS http://localhost:8085/api/slots

# Check resource handlers
grep -r "addResourceHandlers" src/
```

#### **Performance Issues**
```bash
# Check memory usage
free -h
top -p $(pgrep java)

# Check database connections
psql -d smart_parking_db -c "SELECT count(*) FROM pg_stat_activity;"

# Check slow queries
psql -d smart_parking_db -c "SELECT query, mean_time, calls FROM pg_stat_statements ORDER BY mean_time DESC LIMIT 10;"
```

### **Log Analysis**
```bash
# Application logs
tail -f logs/smart-parking-system.log

# Spring Boot specific logs
grep -i "error\|warn" logs/smart-parking-system.log

# Database query logs
grep -i "hibernate\|sql" logs/smart-parking-system.log
```

---

## **📊 Monitoring & Maintenance**

### **Health Checks**
```bash
# Application health
curl http://localhost:8085/actuator/health

# Database health
pg_isready -h localhost -p 5432

# System resources
df -h
free -h
uptime
```

### **Regular Maintenance**
```bash
# Weekly tasks
- Vacuum database: psql -d smart_parking_db -c "VACUUM ANALYZE;"
- Update statistics: psql -d smart_parking_db -c "ANALYZE;"
- Check logs for errors
- Backup database
- Monitor performance metrics

# Monthly tasks
- Update dependencies (mvn versions:display-dependency-updates)
- Security patches
- Performance tuning
- Capacity planning
```

---

## **🚀 Production Checklist**

### **Pre-Deployment Checklist**
- [ ] PostgreSQL 18.2 installed and configured
- [ ] Database created and schema initialized
- [ ] Application properties updated for production
- [ ] SSL certificates configured (if using HTTPS)
- [ ] Firewall rules configured
- [ ] Backup strategy implemented
- [ ] Monitoring tools set up
- [ ] Load testing completed
- [ ] Security audit performed

### **Post-Deployment Checklist**
- [ ] All API endpoints responding correctly
- [ ] Frontend applications loading properly
- [ ] Database connections stable
- [ ] Real-time synchronization working
- [ ] Performance metrics within acceptable range
- [ ] Error rates minimal
- [ ] Backup process verified
- [ ] Monitoring alerts configured

---

## **📞 Support**

For deployment issues:
1. Check this guide first
2. Review application logs
3. Verify database connectivity
4. Test API endpoints individually
5. Check system resources

**🎉 Your Smart Parking System is now ready for production deployment!**
