package com.parking.controller;

import com.parking.dto.ParkingStats;
import com.parking.entity.Booking;
import com.parking.entity.ParkingSlot;
import com.parking.service.BookingService;
import com.parking.service.ParkingSlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:8081"})
public class DashboardController {
    
    private final ParkingSlotService parkingSlotService;
    private final BookingService bookingService;
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        try {
            List<ParkingSlot> slots = parkingSlotService.getAllSlots();
            List<Booking> bookings = bookingService.getAllBookings();
            
            long totalSlots = slots.size();
            long availableSlots = slots.stream().filter(s -> s.getStatus() == ParkingSlot.SlotStatus.EMPTY).count();
            long occupiedSlots = totalSlots - availableSlots;
            double occupancyRate = totalSlots > 0 ? (double) occupiedSlots / totalSlots * 100 : 0;
            
            long totalBookings = bookings.size();
            long activeBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.ACTIVE).count();
            long completedBookings = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED).count();
            
            // Calculate daily revenue (24-hour window)
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
            
            double totalRevenue = bookings.stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED && 
                               b.getAmountCharged() != null &&
                               b.getExitTime() != null &&
                               b.getExitTime().isAfter(startOfDay) && 
                               b.getExitTime().isBefore(endOfDay))
                    .mapToDouble(b -> b.getAmountCharged().doubleValue())
                    .sum();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalSlots", totalSlots);
            stats.put("availableSlots", availableSlots);
            stats.put("occupiedSlots", occupiedSlots);
            stats.put("occupancyRate", Math.round(occupancyRate * 10.0) / 10.0);
            stats.put("totalBookings", totalBookings);
            stats.put("activeBookings", activeBookings);
            stats.put("completedBookings", completedBookings);
            stats.put("totalRevenue", totalRevenue);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching dashboard stats", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/hourly-traffic")
    public ResponseEntity<Map<String, Object>> getHourlyTraffic(
            @RequestParam(defaultValue = "today") String period) {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            Map<Integer, Long> hourlyData = new HashMap<>();
            
            // Initialize all hours with 0
            for (int hour = 6; hour <= 21; hour++) {
                hourlyData.put(hour, 0L);
            }
            
            // Count bookings by hour
            for (Booking booking : bookings) {
                if (booking.getEntryTime() != null) {
                    int hour = booking.getEntryTime().getHour();
                    if (hour >= 6 && hour <= 21) {
                        hourlyData.put(hour, hourlyData.getOrDefault(hour, 0L) + 1);
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("period", period);
            response.put("data", hourlyData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching hourly traffic data", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/floor-stats")
    public ResponseEntity<Map<String, Object>> getFloorStats() {
        try {
            List<ParkingSlot> slots = parkingSlotService.getAllSlots();
            
            Map<String, Object> groundFloor = new HashMap<>();
            Map<String, Object> firstFloor = new HashMap<>();
            
            List<ParkingSlot> groundSlots = slots.stream()
                    .filter(s -> s.getFloor().equals("Ground"))
                    .collect(Collectors.toList());
            
            List<ParkingSlot> firstSlots = slots.stream()
                    .filter(s -> s.getFloor().equals("First"))
                    .collect(Collectors.toList());
            
            groundFloor.put("total", groundSlots.size());
            groundFloor.put("available", groundSlots.stream().filter(s -> s.getStatus() == ParkingSlot.SlotStatus.EMPTY).count());
            groundFloor.put("occupied", groundSlots.stream().filter(s -> s.getStatus() == ParkingSlot.SlotStatus.OCCUPIED).count());
            
            firstFloor.put("total", firstSlots.size());
            firstFloor.put("available", firstSlots.stream().filter(s -> s.getStatus() == ParkingSlot.SlotStatus.EMPTY).count());
            firstFloor.put("occupied", firstSlots.stream().filter(s -> s.getStatus() == ParkingSlot.SlotStatus.OCCUPIED).count());
            
            Map<String, Object> response = new HashMap<>();
            response.put("groundFloor", groundFloor);
            response.put("firstFloor", firstFloor);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching floor stats", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueStats(
            @RequestParam(defaultValue = "today") String period) {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            LocalDateTime now = LocalDateTime.now();
            
            List<Booking> filteredBookings = bookings.stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.COMPLETED && b.getAmountCharged() != null)
                    .collect(Collectors.toList());
            
            // Filter by period for daily revenue (24-hour window)
            if ("today".equals(period)) {
                LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 59);
                
                filteredBookings = filteredBookings.stream()
                    .filter(b -> b.getExitTime() != null &&
                               b.getExitTime().isAfter(startOfDay) && 
                               b.getExitTime().isBefore(endOfDay))
                    .collect(Collectors.toList());
            } else if ("week".equals(period)) {
                LocalDateTime startOfWeek = now.minusDays(7);
                filteredBookings = filteredBookings.stream()
                    .filter(b -> b.getExitTime() != null && b.getExitTime().isAfter(startOfWeek))
                    .collect(Collectors.toList());
            } else if ("month".equals(period)) {
                LocalDateTime startOfMonth = now.minusDays(30);
                filteredBookings = filteredBookings.stream()
                    .filter(b -> b.getExitTime() != null && b.getExitTime().isAfter(startOfMonth))
                    .collect(Collectors.toList());
            }
            
            double totalRevenue = filteredBookings.stream()
                    .mapToDouble(b -> b.getAmountCharged().doubleValue())
                    .sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("period", period);
            response.put("totalRevenue", totalRevenue);
            response.put("bookingCount", filteredBookings.size());
            response.put("averageRevenue", filteredBookings.size() > 0 ? totalRevenue / filteredBookings.size() : 0);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching revenue stats", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/peak-hours")
    public ResponseEntity<Map<String, Object>> getPeakHours() {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            Map<Integer, Long> hourlyCount = new HashMap<>();
            
            // Count bookings by hour
            for (Booking booking : bookings) {
                if (booking.getEntryTime() != null) {
                    int hour = booking.getEntryTime().getHour();
                    hourlyCount.put(hour, hourlyCount.getOrDefault(hour, 0L) + 1);
                }
            }
            
            // Find peak hours
            List<Map.Entry<Integer, Long>> sortedHours = hourlyCount.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                    .limit(3)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("peakHours", sortedHours);
            response.put("data", hourlyCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching peak hours", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/activity-feed")
    public ResponseEntity<List<Map<String, Object>>> getActivityFeed(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Booking> bookings = bookingService.getAllBookings();
            
            List<Map<String, Object>> activities = bookings.stream()
                    .sorted((b1, b2) -> b2.getEntryTime().compareTo(b1.getEntryTime()))
                    .limit(limit)
                    .map(booking -> {
                        Map<String, Object> activity = new HashMap<>();
                        activity.put("id", booking.getBookingId());
                        activity.put("type", booking.getStatus() == Booking.BookingStatus.COMPLETED ? "checkout" : "booking");
                        activity.put("vehicleNumber", booking.getVehicleNumber());
                        activity.put("slotId", booking.getParkingSlot().getSlotId());
                        activity.put("floor", booking.getParkingSlot().getFloor());
                        activity.put("timestamp", booking.getEntryTime());
                        activity.put("status", booking.getStatus());
                        if (booking.getAmountCharged() != null) {
                            activity.put("amount", booking.getAmountCharged());
                        }
                        return activity;
                    })
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            log.error("Error fetching activity feed", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
