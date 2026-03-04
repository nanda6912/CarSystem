package com.parking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Smart Parking Management System Application
 * 
 * Enterprise-grade parking management system with:
 * - Real-time synchronization across terminals
 * - PostgreSQL 18.2 database integration
 * - RESTful API architecture
 * - Professional authentication system
 * - PDF ticket generation
 * - Cross-terminal communication
 * 
 * @version 7.0.0
 * @author Smart Parking Team
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableCaching
public class SmartParkingApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SmartParkingApplication.class);
        
        // Set default profile if none specified
        if (System.getProperty("spring.profiles.active") == null || System.getProperty("spring.profiles.active").isEmpty()) {
            app.setAdditionalProfiles("dev");
        }
        
        app.run(args);
    }
}
