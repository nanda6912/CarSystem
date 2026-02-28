package com.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from Smart Parking API!");
    }
    
    @GetMapping("/slots-simple")
    public ResponseEntity<String> getSlotsSimple() {
        String slots = "[{\"slotId\":\"G1\",\"floor\":\"Ground\",\"status\":\"EMPTY\"},{\"slotId\":\"G2\",\"floor\":\"Ground\",\"status\":\"EMPTY\"}]";
        return ResponseEntity.ok(slots);
    }
}
