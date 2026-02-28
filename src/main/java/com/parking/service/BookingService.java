package com.parking.service;

import com.parking.entity.Booking;
import com.parking.entity.Booking.BookingStatus;
import com.parking.entity.ParkingSlot;
import com.parking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final ParkingSlotService parkingSlotService;
    private static final BigDecimal HOURLY_RATE = new BigDecimal("20.00");
    
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Optional<Booking> getBookingById(String bookingId) {
        return bookingRepository.findByBookingId(bookingId);
    }
    
    public List<Booking> getActiveBookings() {
        return bookingRepository.findByStatus(BookingStatus.ACTIVE);
    }
    
    @Transactional
    public Booking createBooking(String slotId, String vehicleNumber, String phoneNumber) {
        // Normalize vehicle number to uppercase for consistency
        vehicleNumber = vehicleNumber.toUpperCase().trim();
        
        // Check if vehicle with same number is already parked
        Optional<Booking> existingBooking = bookingRepository.findByVehicleNumberAndStatus(
            vehicleNumber, BookingStatus.ACTIVE);
        if (existingBooking.isPresent()) {
            throw new RuntimeException("Vehicle " + vehicleNumber + " is already parked in slot " + 
                existingBooking.get().getParkingSlot().getSlotId());
        }
        
        // Check if slot is available
        if (!parkingSlotService.bookSlot(slotId)) {
            throw new RuntimeException("Slot " + slotId + " is not available");
        }
        
        // Get the parking slot
        Optional<ParkingSlot> slotOpt = parkingSlotService.getSlotById(slotId);
        if (slotOpt.isEmpty()) {
            throw new RuntimeException("Slot " + slotId + " not found");
        }
        
        // Create booking
        String bookingId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setParkingSlot(slotOpt.get());
        booking.setVehicleNumber(vehicleNumber);
        booking.setPhoneNumber(phoneNumber);
        booking.setEntryTime(LocalDateTime.now());
        booking.setStatus(BookingStatus.ACTIVE);
        
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Created booking {} for slot {} with vehicle {}", bookingId, slotId, vehicleNumber);
        
        return savedBooking;
    }
    
    @Transactional
    public Booking checkoutBooking(String bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findByBookingIdAndStatus(bookingId, BookingStatus.ACTIVE);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Active booking with ID " + bookingId + " not found");
        }
        
        Booking booking = bookingOpt.get();
        LocalDateTime exitTime = LocalDateTime.now();
        
        // Calculate duration and amount
        Duration duration = Duration.between(booking.getEntryTime(), exitTime);
        long minutes = duration.toMinutes();
        long hours = (minutes + 59) / 60; // Round up to next hour
        BigDecimal amount = HOURLY_RATE.multiply(new BigDecimal(hours));
        
        // Update booking
        booking.setExitTime(exitTime);
        booking.setAmountCharged(amount);
        booking.setStatus(BookingStatus.COMPLETED);
        
        // Release the slot
        parkingSlotService.releaseSlot(booking.getParkingSlot().getSlotId());
        
        Booking savedBooking = bookingRepository.save(booking);
        log.info("Checked out booking {} with amount {}", bookingId, amount);
        
        return savedBooking;
    }
    
    public BigDecimal calculateAmount(LocalDateTime entryTime, LocalDateTime exitTime) {
        Duration duration = Duration.between(entryTime, exitTime);
        long minutes = duration.toMinutes();
        long hours = (minutes + 59) / 60; // Round up to next hour
        return HOURLY_RATE.multiply(new BigDecimal(hours));
    }
    
    public long getActiveBookingCount() {
        return bookingRepository.countByStatus(BookingStatus.ACTIVE);
    }
}
