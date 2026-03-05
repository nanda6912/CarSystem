# Smart Parking System - Clean Project Structure

## 📁 Essential Files Only

### **Core Application Files**
- `pom.xml` - Maven configuration
- `src/main/java/` - Java source code
- `src/main/resources/` - Configuration and static files
- `src/main/resources/static/` - Frontend HTML files
- `src/main/resources/application.properties` - Production configuration
- `src/main/resources/schema.sql` - Database schema

### **Documentation**
- `README.md` - Project documentation
- `DEPLOYMENT-GUIDE.md` - Deployment instructions
- `PROJECT-STRUCTURE.md` - This file

### **Version Control**
- `.git/` - Git repository
- `.gitignore` - Git ignore rules

## 🚀 Deployment Commands

```bash
# Build application
mvn clean package -DskipTests

# Run application
java -jar target/smart-parking-system-1.0.0.jar

# Access at: http://localhost:8085
```

## 🌐 Application Endpoints

- **Main App**: http://localhost:8085/
- **Public Booking**: http://localhost:8085/public-booking.html
- **Professional Dashboard**: http://localhost:8085/professional-dashboard.html
- **Entry Terminal**: http://localhost:8085/professional-entry-terminal.html
- **Exit Terminal**: http://localhost:8085/professional-exit-terminal.html
- **Authentication**: http://localhost:8085/auth.html

## 🔐 Login Credentials

- **Exit Staff**: username: `exit`, password: `exit123`
- **Entry Staff**: username: `entry`, password: `entry123`
- **Admin**: username: `admin`, password: `admin123`

## 📊 Features

- ✅ 200 parking slots (Ground + First floors)
- ✅ Real-time slot management
- ✅ Booking system with PDF receipts
- ✅ Professional terminals for entry/exit
- ✅ Admin dashboard
- ✅ PostgreSQL database integration
- ✅ Authentication system
- ✅ WebSocket real-time updates
