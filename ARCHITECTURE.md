# 🏗️ Smart Parking System Architecture

## **📋 Overview**

The Smart Parking System follows a **clean, layered architecture** with clear separation of concerns, following industry best practices for enterprise Spring Boot applications.

---

## **🏗️ System Architecture**

### **🎯 High-Level Architecture**
```
┌─────────────────────────────────────────────────────────────────┐
│                    Frontend Layer (Vercel)                        │
├─────────────────────────────────────────────────────────────────┤
│  🎫 Public Booking    🏢 Entry Terminal    💳 Exit Terminal   │
│  📊 Admin Dashboard    🔐 Authentication    📱 Mobile-First     │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼ HTTPS APIs
┌─────────────────────────────────────────────────────────────────┐
│                    Backend Layer (Railway)                       │
├─────────────────────────────────────────────────────────────────┤
│  🎮 Controllers      🧠 Services         📚 Repositories     │
│  🗄️ PostgreSQL 18.2    🔄 Real-time Sync    📊 Analytics        │
│  🔐 JWT Auth          📝 PDF Generation    🚀 REST APIs        │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Data Layer (PostgreSQL)                       │
├─────────────────────────────────────────────────────────────────┤
│  🅿️ parking_slots     📝 bookings           👥 users          │
│  📊 analytics          🔍 audit_logs         💰 billing         │
└─────────────────────────────────────────────────────────────────┘
```

---

## **🔧 Backend Architecture**

### **📦 Package Structure**
```
com.parking/
├── 📄 SmartParkingApplication.java          # Application Entry Point
├── 📁 controller/                           # Presentation Layer
│   ├── 🎮 BookingController.java           # Booking Management APIs
│   ├── 📊 DashboardController.java          # Analytics & Statistics
│   ├── 🅿️ ParkingSlotController.java        # Slot Management APIs
│   └── 🧪 TestController.java               # Health Check & Testing
├── 📁 service/                              # Business Logic Layer
│   ├── 📝 BookingService.java              # Booking Business Logic
│   ├── 🅿️ ParkingSlotService.java          # Slot Management Logic
│   └── 🎫 SimplePDFService.java             # PDF Generation Service
├── 📁 repository/                           # Data Access Layer
│   ├── 📝 BookingRepository.java            # Booking JPA Repository
│   └── 🅿️ ParkingSlotRepository.java      # Slot JPA Repository
├── 📁 entity/                               # Domain Models
│   ├── 📝 Booking.java                      # Booking Entity
│   └── 🅿️ ParkingSlot.java                 # Parking Slot Entity
├── 📁 dto/                                  # Data Transfer Objects
│   ├── 📝 BookingRequest.java               # Booking Request DTO
│   ├── 🅿️ ParkingSlotDTO.java              # Slot Response DTO
│   └── 📊 ParkingStats.java                 # Statistics DTO
├── 📁 config/                               # Configuration Classes
│   ├── 🗄️ DataInitializer.java              # Database Initialization
│   └── 🌐 WebConfig.java                    # Web & CORS Configuration
└── 📁 exception/                           # Exception Handling
    └── ⚠️ GlobalExceptionHandler.java       # Centralized Error Handling
```

---

## **🔄 Layer Responsibilities**

### **🎮 Controller Layer (Presentation)**
- **🎯 Responsibility**: Handle HTTP requests and responses
- **📝 Tasks**: 
  - Request validation
  - Response formatting
  - HTTP status codes
  - API documentation
- **🔗 Dependencies**: Service layer only

### **🧠 Service Layer (Business Logic)**
- **🎯 Responsibility**: Implement business rules and logic
- **📝 Tasks**:
  - Business rule validation
  - Transaction management
  - Business workflows
  - Cross-entity operations
- **🔗 Dependencies**: Repository layer, external services

### **📚 Repository Layer (Data Access)**
- **🎯 Responsibility**: Database operations and data persistence
- **📝 Tasks**:
  - CRUD operations
  - Query optimization
  - Data mapping
  - Transaction boundaries
- **🔗 Dependencies**: Database (PostgreSQL)

### **🗄️ Entity Layer (Domain Models)**
- **🎯 Responsibility**: Represent domain concepts and business rules
- **📝 Tasks**:
  - Business invariants
  - Data relationships
  - Domain-specific methods
  - Validation rules
- **🔗 Dependencies**: Minimal, domain-focused

---

## **🔧 Design Patterns Applied**

### **📝 Repository Pattern**
```java
@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, String> {
    List<ParkingSlot> findByStatus(SlotStatus status);
    List<ParkingSlot> findByFloor(String floor);
    List<ParkingSlot> findByFloorAndStatus(String floor, SlotStatus status);
    long countByStatus(SlotStatus status);
}
```

### **🎯 Service Layer Pattern**
```java
@Service
@Transactional
public class BookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    
    public Booking createBooking(BookingRequest request) {
        // Business logic implementation
    }
}
```

### **📦 DTO Pattern**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotBlank(message = "Slot ID is required")
    private String slotId;
    
    @NotBlank(message = "Vehicle number is required")
    private String vehicleNumber;
    
    // Validation annotations
}
```

### **⚠️ Global Exception Handling**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("NOT_FOUND", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
```

---

## **🔄 Data Flow Architecture**

### **📝 Booking Creation Flow**
```
1. Frontend → POST /api/bookings
2. Controller → @PostMapping("/bookings")
3. Service → bookingService.createBooking(request)
4. Repository → parkingSlotRepository.findById(slotId)
5. Database → PostgreSQL transaction
6. Response → JSON booking object
7. Frontend → Update UI + Cross-terminal sync
```

### **🅿️ Slot Status Update Flow**
```
1. Service → slot.setStatus(OCCUPIED)
2. Repository → parkingSlotRepository.save(slot)
3. Database → PostgreSQL UPDATE
4. Event → Dispatch cross-terminal event
5. Frontend → Real-time update across all terminals
```

---

## **🔐 Security Architecture**

### **🎭 Role-Based Access Control**
```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    // Admin-only endpoints
}

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasAnyRole('ENTRY_STAFF', 'EXIT_STAFF')")
public class StaffController {
    // Staff-only endpoints
}
```

### **🔑 JWT Authentication Flow**
```
1. User Login → /auth/login
2. Validation → Username/Password check
3. Token Generation → JWT with roles
4. Token Storage → localStorage
5. API Calls → Bearer token in Authorization header
6. Token Validation → Spring Security filter
7. Access Granted → Based on roles
```

---

## **🔄 Real-Time Synchronization**

### **📡 Event-Driven Architecture**
```javascript
// Cross-terminal synchronization
class SynchronizationManager {
    dispatchBookingEvent(booking) {
        // localStorage event for cross-tab sync
        localStorage.setItem('newBooking', JSON.stringify(booking));
        
        // Custom event for same-tab listeners
        window.dispatchEvent(new CustomEvent('bookingCreated', {
            detail: booking
        }));
    }
}
```

### **🔄 Event Types**
- **`newBooking`**: New booking created
- **`bookingCompleted`**: Booking checkout processed
- **`slotStatusChanged`**: Slot status updated
- **`publicBookingCreated`**: Public booking created
- **`exitCheckoutCompleted`**: Exit checkout completed

---

## **📊 Database Architecture**

### **🗄️ PostgreSQL Schema Design**
```sql
-- Parking Slots Table
CREATE TABLE parking_slots (
    slot_id VARCHAR(10) PRIMARY KEY,
    floor VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings Table
CREATE TABLE bookings (
    booking_id VARCHAR(36) PRIMARY KEY,
    slot_id VARCHAR(10) REFERENCES parking_slots(slot_id),
    vehicle_number VARCHAR(20) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    checkout_time TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### **🔍 Database Indexes**
```sql
-- Performance indexes
CREATE INDEX idx_booking_status ON bookings(status);
CREATE INDEX idx_booking_time ON bookings(booking_time);
CREATE INDEX idx_slot_status ON parking_slots(status);
CREATE INDEX idx_slot_floor ON parking_slots(floor);
```

---

## **🚀 Deployment Architecture**

### **🏗️ Railway (Backend)**
```
┌─────────────────────────────────────────┐
│              Railway                    │
├─────────────────────────────────────────┤
│  🐘 PostgreSQL (Managed)               │
│  ⚡ Auto-scaling                       │
│  🔒 HTTPS + SSL                        │
│  📊 Monitoring & Logs                  │
│  🔄 CI/CD Integration                  │
└─────────────────────────────────────────┘
```

### **🌐 Vercel (Frontend)**
```
┌─────────────────────────────────────────┐
│               Vercel                    │
├─────────────────────────────────────────┤
│  🌍 Global CDN                         │
│  📱 Edge Caching                       │
│  🔒 Automatic HTTPS                   │
│  📊 Analytics & Monitoring            │
│  🔄 Instant Rollbacks                  │
└─────────────────────────────────────────┘
```

---

## **🔧 Configuration Management**

### **⚙️ Environment-Specific Configuration**
```properties
# Development (application.properties)
spring.profiles.active=dev
spring.jpa.show-sql=true
logging.level.com.parking=DEBUG

# Production (application-prod.properties)
spring.profiles.active=prod
spring.jpa.show-sql=false
logging.level.com.parking=INFO

# Railway (application-railway.properties)
spring.profiles.active=railway
spring.datasource.url=${RAILWAY_DATABASE_URL}
```

### **🔐 Environment Variables**
```bash
# Database Configuration
DATABASE_URL=postgresql://user:pass@host:port/db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=secure_password

# Application Configuration
FRONTEND_URL=https://your-app.vercel.app
JWT_SECRET=your-secret-key
PORT=8085
```

---

## **🧪 Testing Architecture**

### **📝 Test Pyramid**
```
        🔺 E2E Tests (5%)
       🔺🔺 Integration Tests (15%)
      🔺🔺🔺 Unit Tests (80%)
```

### **🧪 Test Categories**
- **📝 Unit Tests**: Service layer, repository layer
- **🔗 Integration Tests**: Database operations, API endpoints
- **🌐 End-to-End Tests**: Full user workflows
- **⚡ Performance Tests**: Load testing, stress testing

### **📊 Test Coverage**
- **🎯 Target**: 80%+ code coverage
- **🔧 Tools**: JaCoCo, Mockito, TestContainers
- **📈 Reports**: Coverage reports, test metrics

---

## **📊 Monitoring & Observability**

### **🔍 Health Checks**
```java
@Component
public class ParkingSystemHealthIndicator implements HealthIndicator {
    @Override
    public Health health() {
        // Check database connectivity
        // Check external services
        // Return system health status
    }
}
```

### **📊 Metrics Collection**
- **📈 Booking Metrics**: Volume, duration, revenue
- **🅿️ Slot Metrics**: Occupancy, availability, turnover
- **⚡ Performance Metrics**: Response times, throughput
- **🔐 Security Metrics**: Authentication success/failure rates

---

## **🔄 CI/CD Architecture**

### **🚀 Pipeline Stages**
```
1. Code Commit → GitHub Repository
2. Automated Tests → Unit + Integration Tests
3. Security Scan → OWASP + Dependency Check
4. Build Application → Maven Package
5. Deploy Backend → Railway (Production)
6. Deploy Frontend → Vercel (Production)
7. Health Checks → Application Validation
8. Notification → Success/Failure Alert
```

### **🔄 Deployment Strategies**
- **🔄 Blue-Green Deployment**: Zero downtime
- **🚀 Canary Releases**: Gradual rollout
- **⚡ Rollback Capability**: Quick recovery

---

## **🎯 Best Practices Applied**

### **🏗️ Architecture Principles**
- **🎯 Single Responsibility**: Each class has one reason to change
- **🔄 Open/Closed**: Open for extension, closed for modification
- **🔗 Dependency Inversion**: Depend on abstractions, not concretions
- **📝 Interface Segregation**: Small, focused interfaces

### **🔒 Security Principles**
- **🔐 Least Privilege**: Minimum required permissions
- **🛡️ Defense in Depth**: Multiple security layers
- **🔒 Secure by Default**: Secure configurations out of the box
- **📝 Audit Trail**: Comprehensive logging and monitoring

### **⚡ Performance Principles**
- **🔄 Caching Strategy**: Appropriate caching at multiple levels
- **📊 Database Optimization**: Efficient queries and indexing
- **🌐 Resource Management**: Connection pooling and resource cleanup
- **📈 Scalability Design**: Horizontal and vertical scaling

---

## **🎉 Architecture Benefits**

### **✅ Maintainability**
- **🎯 Clear Separation**: Easy to understand and modify
- **📝 Well-Documented**: Comprehensive documentation
- **🧪 Testable**: High test coverage and testability
- **🔄 Modular**: Independent, replaceable components

### **🚀 Scalability**
- **⚡ Performance**: Optimized for high load
- **📊 Monitoring**: Real-time performance metrics
- **🔄 Auto-scaling**: Handle traffic spikes
- **🌐 Global Distribution**: CDN and edge caching

### **🔒 Security**
- **🛡️ Multi-Layer**: Defense in depth security
- **🔐 Authentication**: Role-based access control
- **📝 Audit Trail**: Comprehensive logging
- **⚠️ Threat Protection**: OWASP compliance

### **🔄 Reliability**
- **🔍 Health Checks**: Proactive monitoring
- **🚀 CI/CD**: Automated testing and deployment
- **⚡ Rollback**: Quick recovery from failures
- **📊 Observability**: Complete system visibility

---

## **📞 Architecture Documentation**

This architecture document serves as the definitive guide for:
- **👥 Development Team**: Understanding system design
- **🔧 Operations Team**: Deployment and maintenance
- **🔍 QA Team**: Testing strategy and coverage
- **📊 Management Team**: System capabilities and limitations

**🎉 The Smart Parking System architecture is designed for enterprise-grade reliability, scalability, and maintainability!**
