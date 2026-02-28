package com.parking.service;

import com.parking.entity.ParkingSlot;
import com.parking.entity.ParkingSlot.SlotStatus;
import com.parking.repository.ParkingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParkingSlotService {
    
    private final ParkingSlotRepository parkingSlotRepository;
    
    public List<ParkingSlot> getAllSlots() {
        return parkingSlotRepository.findAll();
    }
    
    public Optional<ParkingSlot> getSlotById(String slotId) {
        return parkingSlotRepository.findById(slotId);
    }
    
    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findByStatus(SlotStatus.EMPTY);
    }
    
    public List<ParkingSlot> getSlotsByFloor(String floor) {
        return parkingSlotRepository.findByFloor(floor);
    }
    
    public List<ParkingSlot> getAvailableSlotsByFloor(String floor) {
        return parkingSlotRepository.findByFloorAndStatus(floor, SlotStatus.EMPTY);
    }
    
    @Transactional
    public boolean bookSlot(String slotId) {
        Optional<ParkingSlot> slotOpt = parkingSlotRepository.findBySlotIdAndStatus(slotId, SlotStatus.EMPTY);
        if (slotOpt.isPresent()) {
            ParkingSlot slot = slotOpt.get();
            slot.setStatus(SlotStatus.OCCUPIED);
            parkingSlotRepository.save(slot);
            log.info("Slot {} booked successfully", slotId);
            return true;
        }
        log.warn("Slot {} is not available for booking", slotId);
        return false;
    }
    
    @Transactional
    public boolean releaseSlot(String slotId) {
        Optional<ParkingSlot> slotOpt = parkingSlotRepository.findById(slotId);
        if (slotOpt.isPresent()) {
            ParkingSlot slot = slotOpt.get();
            slot.setStatus(SlotStatus.EMPTY);
            parkingSlotRepository.save(slot);
            log.info("Slot {} released successfully", slotId);
            return true;
        }
        log.warn("Slot {} not found for release", slotId);
        return false;
    }
    
    @Transactional
    public void initializeSlots() {
        if (parkingSlotRepository.count() == 0) {
            // Generate slots for ground floor (GA001-GE020, GB001-GE020, etc.)
            String[] blocks = {"A", "B", "C", "D", "E"};
            int slotsPerBlock = 20;
            
            // Ground Floor slots
            for (String block : blocks) {
                for (int i = 1; i <= slotsPerBlock; i++) {
                    String slotId = String.format("G%s%02d", block, i);
                    parkingSlotRepository.save(new ParkingSlot(slotId, "Ground", SlotStatus.EMPTY));
                }
            }
            
            // First Floor slots
            for (String block : blocks) {
                for (int i = 1; i <= slotsPerBlock; i++) {
                    String slotId = String.format("F%s%02d", block, i);
                    parkingSlotRepository.save(new ParkingSlot(slotId, "First", SlotStatus.EMPTY));
                }
            }
            
            log.info("Initialized 200 parking slots (100 ground floor, 100 first floor) in blocks A-E");
        }
    }
    
    public long getAvailableSlotCount() {
        return parkingSlotRepository.countByStatus(SlotStatus.EMPTY);
    }
    
    public long getOccupiedSlotCount() {
        return parkingSlotRepository.countByStatus(SlotStatus.OCCUPIED);
    }
}
