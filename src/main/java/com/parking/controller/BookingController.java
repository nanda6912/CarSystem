package com.parking.controller;

import com.parking.entity.Booking;
import com.parking.service.BookingService;
import com.parking.service.SimplePDFService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:8081"})
public class BookingController {
    
    private final BookingService bookingService;
    private final SimplePDFService pdfService;
    
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    
    @GetMapping("/{bookingId}")
    public ResponseEntity<Booking> getBookingById(@PathVariable String bookingId) {
        Optional<Booking> booking = bookingService.getBookingById(bookingId);
        return booking.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        List<Booking> activeBookings = bookingService.getActiveBookings();
        return ResponseEntity.ok(activeBookings);
    }
    
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody BookingRequest request) {
        try {
            Booking booking = bookingService.createBooking(
                request.slotId(),
                request.vehicleNumber(),
                request.phoneNumber()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (RuntimeException e) {
            log.error("Error creating booking: {}", e.getMessage());
            // Return detailed error message
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{bookingId}/checkout")
    public ResponseEntity<Booking> checkoutBooking(@PathVariable String bookingId) {
        try {
            Booking booking = bookingService.checkoutBooking(bookingId);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            log.error("Error checking out booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{bookingId}/ticket")
    public ResponseEntity<byte[]> downloadTicket(@PathVariable String bookingId) {
        Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            byte[] ticketContent = pdfService.generateTicket(bookingOpt.get());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDispositionFormData("attachment", "parking-ticket-" + bookingId + ".txt");
            headers.setContentLength(ticketContent.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(ticketContent);
        } catch (Exception e) {
            log.error("Error generating PDF ticket for booking {}", bookingId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/{bookingId}/receipt")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable String bookingId) {
        Optional<Booking> bookingOpt = bookingService.getBookingById(bookingId);
        if (bookingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Booking booking = bookingOpt.get();
        if (booking.getExitTime() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            byte[] pdfContent = pdfService.generateReceipt(booking);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "parking-receipt-" + bookingId + ".pdf");
            headers.setContentLength(pdfContent.length);
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
        } catch (Exception e) {
            log.error("Error generating PDF receipt for booking {}", bookingId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    public record BookingRequest(
        @NotBlank(message = "Slot ID is required")
        String slotId,
        
        @NotBlank(message = "Vehicle number is required")
        @Pattern(regexp = "^[A-Za-z0-9]{1,15}$", message = "Vehicle number must be alphanumeric and max 15 characters")
        @Size(max = 15, message = "Vehicle number cannot exceed 15 characters")
        String vehicleNumber,
        
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
        String phoneNumber
    ) {}
}
