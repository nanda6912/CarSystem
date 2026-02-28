package com.parking.controller;

import com.parking.dto.ParkingSlotDTO;
import com.parking.dto.ParkingStats;
import com.parking.entity.ParkingSlot;
import com.parking.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
}
