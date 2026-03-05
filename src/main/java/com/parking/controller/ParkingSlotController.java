package com.parking.controller;

import com.parking.dto.ParkingSlotDTO;
import com.parking.dto.ParkingStats;
import com.parking.entity.ParkingSlot;
import com.parking.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/slots")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:8081"})
public class ParkingSlotController {
    
    private final ParkingSlotService parkingSlotService;
    
    @GetMapping
    public ResponseEntity<List<ParkingSlot>> getAllSlots() {
        List<ParkingSlot> slots = parkingSlotService.getAllSlots();
        return ResponseEntity.ok(slots);
    }
    
    @GetMapping("/{slotId}")
    public ResponseEntity<ParkingSlotDTO> getSlotById(@PathVariable String slotId) {
        Optional<ParkingSlot> slot = parkingSlotService.getSlotById(slotId);
        return slot.map(s -> ResponseEntity.ok(ParkingSlotDTO.fromEntity(s)))
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<ParkingSlotDTO>> getAvailableSlots() {
        List<ParkingSlot> availableSlots = parkingSlotService.getAvailableSlots();
        List<ParkingSlotDTO> slotDTOs = availableSlots.stream()
                .map(ParkingSlotDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(slotDTOs);
    }
    
    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<ParkingSlotDTO>> getSlotsByFloor(@PathVariable String floor) {
        List<ParkingSlot> slots = parkingSlotService.getSlotsByFloor(floor);
        List<ParkingSlotDTO> slotDTOs = slots.stream()
                .map(ParkingSlotDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(slotDTOs);
    }
    
    @GetMapping("/floor/{floor}/available")
    public ResponseEntity<List<ParkingSlotDTO>> getAvailableSlotsByFloor(@PathVariable String floor) {
        List<ParkingSlot> availableSlots = parkingSlotService.getAvailableSlotsByFloor(floor);
        List<ParkingSlotDTO> slotDTOs = availableSlots.stream()
                .map(ParkingSlotDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(slotDTOs);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<ParkingStats> getParkingStats() {
        long availableCount = parkingSlotService.getAvailableSlotCount();
        long occupiedCount = parkingSlotService.getOccupiedSlotCount();
        long totalSlots = availableCount + occupiedCount;
        
        ParkingStats stats = new ParkingStats(totalSlots, availableCount, occupiedCount);
        return ResponseEntity.ok(stats);
    }

    // Authentication endpoints
    private static final Map<String, String> USERS = new HashMap<>();
    
    static {
        // Default credentials (username: password)
        USERS.put("admin", "admin123");
        USERS.put("exit", "exit123");
        USERS.put("entry", "entry123");
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        String role = loginRequest.get("role");

        log.info("Login attempt for user: {} with role: {}", username, role);

        // Validate credentials
        if (username == null || password == null || role == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Missing required fields"
            ));
        }

        // Check if credentials are valid
        String expectedPassword = USERS.get(role);
        if (expectedPassword == null || !expectedPassword.equals(password)) {
            log.warn("Invalid login attempt for role: {}", role);
            return ResponseEntity.status(401).body(Map.of(
                "success", false,
                "message", "Invalid credentials"
            ));
        }

        // Successful login
        log.info("Successful login for user: {} with role: {}", username, role);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Login successful",
            "user", Map.of(
                "username", username,
                "role", role
            )
        ));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> logoutRequest) {
        String role = logoutRequest.get("role");
        log.info("Logout for role: {}", role);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Logout successful"
        ));
    }
}
