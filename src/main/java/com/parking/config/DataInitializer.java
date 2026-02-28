package com.parking.config;

import com.parking.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final ParkingSlotService parkingSlotService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing parking slots...");
        parkingSlotService.initializeSlots();
        log.info("Parking slots initialization completed");
    }
}
