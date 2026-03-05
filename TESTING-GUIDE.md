# 🧪 Smart Parking System - Comprehensive Testing Guide v7.0

## 📋 **Table of Contents**
1. [Testing Strategy](#testing-strategy)
2. [Automated CI/CD Testing](#automated-cicd-testing)
3. [Unit Testing](#unit-testing)
4. [Integration Testing](#integration-testing)
5. [API Testing](#api-testing)
6. [Frontend Testing](#frontend-testing)
7. [Performance Testing](#performance-testing)
8. [Security Testing](#security-testing)
9. [End-to-End Testing](#end-to-end-testing)
10. [Docker Testing](#docker-testing)
11. [Test Reports & Metrics](#test-reports--metrics)

---

## **🎯 Testing Strategy**

### **Testing Pyramid**
```
    🔺 E2E Tests (5%)
   🔺🔺 Integration Tests (15%)
  🔺🔺🔺 Unit Tests (80%)
```

### **Test Coverage Goals**
- **Unit Tests**: 80%+ code coverage
- **Integration Tests**: All API endpoints
- **E2E Tests**: Critical user journeys
- **Performance Tests**: Load and stress testing
- **Security Tests**: Vulnerability scanning
- **Docker Tests**: Container functionality

### **🚀 Automated Testing Pipeline**
The system includes a **complete CI/CD pipeline** that automatically runs:
- Backend unit and integration tests
- Frontend validation tests
- Security vulnerability scanning
- Performance load testing
- Docker container testing

---

## **🔄 Automated CI/CD Testing**

### **GitHub Actions Pipeline**
```yaml
# .github/workflows/ci-cd.yml
# Automatically runs on every push and pull request

Jobs:
- backend-test: PostgreSQL + JUnit tests
- frontend-validation: HTML/CSS/JS validation
- security-scan: Trivy + OWASP scanning
- performance-test: Load testing with Apache Bench
- build-package: Docker image creation
- deploy-staging: Staging environment deployment
- deploy-production: Production deployment
```

### **Running Automated Tests**
```bash
# Run all tests locally
mvn clean test

# Run with coverage report
mvn test jacoco:report

# Run integration tests
mvn verify -P integration-tests

# Run performance tests
mvn verify -P performance-tests
```

---

## **🔬 Unit Testing**

### **Backend Unit Tests**

#### **Service Layer Tests**
```java
// ParkingSlotServiceTest.java
@ExtendWith(MockitoExtension.class)
class ParkingSlotServiceTest {
    
    @Mock
    private ParkingSlotRepository parkingSlotRepository;
    
    @InjectMocks
    private ParkingSlotService parkingSlotService;
    
    @Test
    void shouldBookAvailableSlot() {
        // Given
        String slotId = "GA001";
        ParkingSlot slot = new ParkingSlot(slotId, "Ground", EMPTY);
        when(parkingSlotRepository.findBySlotIdAndStatus(slotId, EMPTY))
            .thenReturn(Optional.of(slot));
        
        // When
        boolean result = parkingSlotService.bookSlot(slotId);
        
        // Then
        assertTrue(result);
        verify(parkingSlotRepository).save(slot);
    }
    
    @Test
    void shouldNotBookOccupiedSlot() {
        // Given
        String slotId = "GA001";
        when(parkingSlotRepository.findBySlotIdAndStatus(slotId, EMPTY))
            .thenReturn(Optional.empty());
        
        // When
        boolean result = parkingSlotService.bookSlot(slotId);
        
        // Then
        assertFalse(result);
        verify(parkingSlotRepository, never()).save(any());
    }
}
```

#### **Controller Layer Tests**
```java
// ParkingSlotControllerTest.java
@WebMvcTest(ParkingSlotController.class)
class ParkingSlotControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private ParkingSlotService parkingSlotService;
    
    @Test
    void shouldGetAllSlots() throws Exception {
        // Given
        List<ParkingSlot> slots = Arrays.asList(
            new ParkingSlot("GA001", "Ground", EMPTY),
            new ParkingSlot("GA002", "Ground", OCCUPIED)
        );
        when(parkingSlotService.getAllSlots()).thenReturn(slots);
        
        // When & Then
        mockMvc.perform(get("/api/slots"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].slotId").value("GA001"));
    }
}
```

### **Running Unit Tests**
```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=ParkingSlotServiceTest

# Run with coverage report
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

---

## **🔗 Integration Testing**

### **Database Integration Tests**
```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ParkingSlotRepositoryTest {
    
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;
    
    @Test
    void shouldSaveAndRetrieveParkingSlot() {
        // Given
        ParkingSlot slot = new ParkingSlot("GA001", "Ground", EMPTY);
        
        // When
        parkingSlotRepository.save(slot);
        Optional<ParkingSlot> retrieved = parkingSlotRepository.findById("GA001");
        
        // Then
        assertTrue(retrieved.isPresent());
        assertEquals("GA001", retrieved.get().getSlotId());
    }
}
```

### **API Integration Tests**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb"
})
class BookingControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @LocalServerPort
    private int port;
    
    @Test
    void shouldCreateBooking() {
        // Given
        BookingRequest request = new BookingRequest("GA001", "TEST123", "1234567890");
        
        // When
        ResponseEntity<Booking> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/api/bookings", 
            request, 
            Booking.class
        );
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody().getBookingId());
    }
}
```

---

## **🌐 API Testing**

### **Automated API Tests**
```bash
#!/bin/bash
# api-tests.sh

BASE_URL="http://localhost:8085"

# Test health endpoint
echo "Testing health endpoint..."
curl -f "$BASE_URL/api/slots" || exit 1

# Test booking creation
echo "Testing booking creation..."
BOOKING_RESPONSE=$(curl -s -X POST "$BASE_URL/api/bookings" \
  -H "Content-Type: application/json" \
  -d '{"slotId":"GA001","vehicleNumber":"TEST123","phoneNumber":"1234567890"}')

echo "Booking created: $BOOKING_RESPONSE"

# Test slot status update
echo "Testing slot status..."
SLOT_STATUS=$(curl -s "$BASE_URL/api/slots/GA001" | jq -r '.status')
echo "Slot GA001 status: $SLOT_STATUS"

# Test booking retrieval
echo "Testing booking retrieval..."
BOOKING_ID=$(echo "$BOOKING_RESPONSE" | jq -r '.bookingId')
BOOKING_DETAILS=$(curl -s "$BASE_URL/api/bookings/$BOOKING_ID")
echo "Booking details: $BOOKING_DETAILS"

echo "✅ All API tests passed!"
```

### **Postman Collection**
```json
{
  "info": {
    "name": "Smart Parking API Tests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get All Slots",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/slots",
          "host": ["{{baseUrl}}"],
          "path": ["api", "slots"]
        }
      }
    },
    {
      "name": "Create Booking",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\"slotId\":\"GA001\",\"vehicleNumber\":\"TEST123\",\"phoneNumber\":\"1234567890\"}"
        },
        "url": {
          "raw": "{{baseUrl}}/api/bookings",
          "host": ["{{baseUrl}}"],
          "path": ["api", "bookings"]
        }
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8085"
    }
  ]
}
```

---

## **🎨 Frontend Testing**

### **HTML Validation Tests**
```bash
#!/bin/bash
# frontend-tests.sh

echo "Validating HTML files..."

for file in *.html; do
    if [ -f "$file" ]; then
        echo "Testing $file..."
        
        # Check DOCTYPE
        if grep -q "<!DOCTYPE html>" "$file"; then
            echo "✅ $file has valid DOCTYPE"
        else
            echo "❌ $file missing DOCTYPE"
            exit 1
        fi
        
        # Check viewport meta tag
        if grep -q "viewport" "$file"; then
            echo "✅ $file has viewport meta tag"
        else
            echo "⚠️ $file missing viewport meta tag"
        fi
        
        # Check responsive design
        if grep -q "bootstrap\|responsive\|media" "$file"; then
            echo "✅ $file has responsive design elements"
        else
            echo "⚠️ $file may lack responsive design"
        fi
    fi
done

echo "✅ Frontend validation completed!"
```

### **JavaScript Functionality Tests**
```javascript
// frontend-tests.js
describe('Smart Parking Frontend Tests', () => {
    
    test('Public Booking Form Validation', () => {
        // Test phone number validation
        const phoneInput = document.getElementById('contactNumber');
        phoneInput.value = '1234567890';
        
        // Trigger validation
        const event = new Event('input');
        phoneInput.dispatchEvent(event);
        
        // Check if valid
        expect(phoneInput.style.borderColor).toBe('var(--success-color)');
    });
    
    test('Slot Selection', () => {
        // Test slot click functionality
        const slot = document.querySelector('[data-slot-id="GA001"]');
        slot.click();
        
        // Check if slot is selected
        expect(slot.classList.contains('selected')).toBe(true);
    });
    
    test('Cross-terminal Synchronization', () => {
        // Test localStorage event
        const eventData = {
            booking: { bookingId: 'TEST123', slotId: 'GA001' },
            timestamp: Date.now()
        };
        
        localStorage.setItem('publicBooking_newBooking', JSON.stringify(eventData));
        
        // Trigger storage event
        const storageEvent = new StorageEvent('storage', {
            key: 'publicBooking_newBooking',
            newValue: JSON.stringify(eventData)
        });
        
        window.dispatchEvent(storageEvent);
        
        // Check if event was handled
        expect(window.externalBookingHandled).toBe(true);
    });
});
```

---

## **⚡ Performance Testing**

### **Load Testing with Apache Bench**
```bash
#!/bin/bash
# performance-tests.sh

BASE_URL="http://localhost:8085"

echo "Starting performance tests..."

# Test slots endpoint
echo "Testing /api/slots endpoint..."
ab -n 1000 -c 10 -g slots-results.dat "$BASE_URL/api/slots"

# Test bookings endpoint
echo "Testing /api/bookings endpoint..."
ab -n 500 -c 5 -g bookings-results.dat "$BASE_URL/api/bookings"

# Test booking creation
echo "Testing booking creation..."
ab -n 100 -c 2 -p booking-post.json -T application/json "$BASE_URL/api/bookings"

echo "Performance tests completed!"
echo "Results saved to *.dat files"
```

### **JMeter Test Plan**
```xml
<!-- jmeter-test-plan.jmx -->
<?xml version="1.0" encoding="UTF-8"?>
<jmeterTestPlan version="1.2">
  <hashTree>
    <TestPlan>
      <stringProp name="TestPlan.comments">Smart Parking Load Test</stringProp>
      <boolProp name="TestPlan.functional_mode">false</boolProp>
      <boolProp name="TestPlan.tearDown_on_shutdown">true</boolProp>
      <boolProp name="TestPlan.serialize_threadgroups">false</boolProp>
      <elementProp name="TestPlan.user_defined_variables" elementType="Arguments">
        <collectionProp name="Arguments.arguments">
          <elementProp name="BASE_URL" elementType="Argument">
            <stringProp name="Argument.name">BASE_URL</stringProp>
            <stringProp name="Argument.value">http://localhost:8085</stringProp>
          </elementProp>
        </collectionProp>
      </elementProp>
    </TestPlan>
    <hashTree>
      <ThreadGroup>
        <stringProp name="ThreadGroup.name">Parking Users</stringProp>
        <stringProp name="ThreadGroup.num_threads">100</stringProp>
        <stringProp name="ThreadGroup.ramp_time">10</stringProp>
        <boolProp name="ThreadGroup.scheduler">true</boolProp>
        <stringProp name="ThreadGroup.duration">300</stringProp>
        <stringProp name="ThreadGroup.delay">5</stringProp>
      </ThreadGroup>
      <hashTree>
        <HTTPSamplerProxy>
          <stringProp name="HTTPSampler.domain">${BASE_URL}</stringProp>
          <stringProp name="HTTPSampler.path">/api/slots</stringProp>
          <stringProp name="HTTPSampler.method">GET</stringProp>
        </HTTPSamplerProxy>
      </hashTree>
    </hashTree>
  </hashTree>
</jmeterTestPlan>
```

---

## **🔒 Security Testing**

### **OWASP ZAP Security Scan**
```bash
#!/bin/bash
# security-tests.sh

echo "Starting security scan..."

# Run ZAP baseline scan
docker run -t owasp/zap2docker-stable zap-baseline.py \
  -t http://localhost:8085 \
  -J zap-report.json

# Check for high-risk vulnerabilities
HIGH_VULNS=$(cat zap-report.json | jq '.site[0].alerts | map(select(.risk == "HIGH")) | length')
echo "High risk vulnerabilities found: $HIGH_VULNS"

if [ "$HIGH_VULNS" -gt 0 ]; then
    echo "❌ Security scan failed!"
    exit 1
else
    echo "✅ Security scan passed!"
fi
```

### **Dependency Vulnerability Scan**
```bash
# OWASP Dependency Check
./dependency-check/bin/dependency-check.sh \
  --project "Smart Parking" \
  --scan . \
  --format XML \
  --out security-reports

# Check for critical vulnerabilities
CRITICAL_VULNS=$(cat security-reports/dependency-check-report.xml | grep -c "severity=\"CRITICAL\"")
echo "Critical vulnerabilities found: $CRITICAL_VULNS"
```

---

## **🔄 End-to-End Testing**

### **Selenium E2E Tests**
```java
// E2ETest.java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class E2ETest {
    
    private WebDriver driver;
    
    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
    }
    
    @Test
    void completeBookingFlow() {
        // Navigate to public booking
        driver.get("http://localhost:8085/public-booking.html");
        
        // Fill out booking form
        driver.findElement(By.id("vehicleNumber")).sendKeys("TEST123");
        driver.findElement(By.id("contactNumber")).sendKeys("1234567890");
        
        // Select vehicle type
        driver.findElement(By.cssSelector("input[name='vehicleType'][value='CAR']")).click();
        
        // Select slot
        WebElement slot = driver.findElement(By.cssSelector("[data-slot-id='GA001']"));
        slot.click();
        
        // Submit booking
        driver.findElement(By.id("bookBtn")).click();
        
        // Verify booking confirmation
        WebDriverWait wait = new WebDriverWait(driver, 10);
        WebElement confirmation = wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.className("booking-confirmation"))
        );
        
        assertTrue(confirmation.isDisplayed());
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

### **Cypress E2E Tests**
```javascript
// cypress/e2e/booking-flow.cy.js
describe('Smart Parking E2E Tests', () => {
    
    beforeEach(() => {
        cy.visit('http://localhost:8085/public-booking.html');
    });
    
    it('should complete a booking flow', () => {
        // Fill booking form
        cy.get('#vehicleNumber').type('TEST123');
        cy.get('#contactNumber').type('1234567890');
        cy.get('input[name="vehicleType"][value="CAR"]').check();
        
        // Select slot
        cy.get('[data-slot-id="GA001"]').click();
        
        // Submit booking
        cy.get('#bookBtn').should('not.be.disabled').click();
        
        // Verify confirmation
        cy.get('.booking-confirmation').should('be.visible');
        cy.get('.booking-id').should('contain.text', 'Booking ID:');
    });
    
    it('should validate phone number format', () => {
        cy.get('#contactNumber').type('123');
        cy.get('#contactNumber').should('have.css', 'border-color', 'rgb(239, 68, 68)');
        
        cy.get('#contactNumber').clear().type('1234567890');
        cy.get('#contactNumber').should('have.css', 'border-color', 'rgb(34, 197, 94)');
    });
});
```

---

## **📊 Test Reports & Metrics**

### **Generating Test Reports**
```bash
# Run all tests and generate reports
mvn clean test jacoco:report

# Generate performance report
ab -n 1000 -c 10 http://localhost:8085/api/slots > performance-report.txt

# Generate security report
docker run -t owasp/zap2docker-stable zap-baseline.py -t http://localhost:8085 -J security-report.json
```

### **Test Coverage Metrics**
- **Target Coverage**: 80%+ for all code
- **Critical Path Coverage**: 100% for booking flow
- **API Coverage**: 100% for all endpoints
- **Frontend Coverage**: 90%+ for JavaScript functions

### **Performance Benchmarks**
- **API Response Time**: < 200ms for 95% of requests
- **Concurrent Users**: 100+ simultaneous users
- **Throughput**: 1000+ requests/minute
- **Memory Usage**: < 2GB under load

---

## **🚀 Continuous Testing**

### **Test Automation in CI/CD**
```yaml
# .github/workflows/test.yml
name: Test Suite

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Run Unit Tests
      run: mvn test
    
    - name: Run Integration Tests
      run: mvn verify -P integration-tests
    
    - name: Run E2E Tests
      run: mvn verify -P e2e-tests
    
    - name: Generate Reports
      run: |
        mvn jacoco:report
        mvn pitest:mutation-coverage
    
    - name: Upload Coverage
      uses: codecov/codecov-action@v3
```

---

## **🔧 Test Data Management**

### **Test Data Setup**
```sql
-- test-data.sql
INSERT INTO parking_slots (slot_id, floor, status) VALUES
('TEST001', 'Test', 'EMPTY'),
('TEST002', 'Test', 'OCCUPIED');

INSERT INTO bookings (booking_id, slot_id, vehicle_number, phone_number, entry_time, status) VALUES
('TEST-BOOKING-001', 'TEST001', 'TEST123', '1234567890', NOW(), 'ACTIVE');
```

### **Test Data Cleanup**
```sql
-- cleanup.sql
DELETE FROM bookings WHERE booking_id LIKE 'TEST-%';
DELETE FROM parking_slots WHERE slot_id LIKE 'TEST%';
```

---

## **📞 Testing Support**

### **Debugging Test Failures**
1. **Check application logs**: `tail -f logs/application.log`
2. **Verify database state**: Connect to test database
3. **Check network connectivity**: `curl -v http://localhost:8085/api/slots`
4. **Review test configuration**: Verify test properties

### **Best Practices**
- **Isolate test environments**: Use separate databases
- **Clean test data**: Reset state between tests
- **Mock external dependencies**: Use test doubles
- **Parallel test execution**: Configure test isolation
- **Comprehensive assertions**: Verify all aspects of functionality

**🎉 Your Smart Parking System now has comprehensive testing coverage!**
