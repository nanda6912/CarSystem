package com.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    // Simple in-memory user store (in production, use a proper database)
    private static final Map<String, String> USERS = new HashMap<>();
    
    static {
        // Default credentials (username: password)
        USERS.put("admin", "admin123");
        USERS.put("exit", "exit123");
        USERS.put("entry", "entry123");
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of(
            "message", "Auth controller is working",
            "status", "OK"
        ));
    }

    @PostMapping("/login")
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> logoutRequest) {
        String role = logoutRequest.get("role");
        log.info("Logout for role: {}", role);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Logout successful"
        ));
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateSession(@RequestParam String role) {
        // In a real application, you would validate the session token
        // For now, just check if the role is valid
        if (USERS.containsKey(role)) {
            return ResponseEntity.ok(Map.of(
                "valid", true,
                "message", "Session is valid"
            ));
        }
        
        return ResponseEntity.status(401).body(Map.of(
            "valid", false,
            "message", "Invalid session"
        ));
    }
}
