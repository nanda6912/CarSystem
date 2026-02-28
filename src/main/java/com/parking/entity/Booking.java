package com.parking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    
    @Id
    private String bookingId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "slot_id", nullable = false)
    private ParkingSlot parkingSlot;
    
    @Column(name = "vehicle_number", nullable = false)
    private String vehicleNumber;
    
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;
    
    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;
    
    @Column(name = "exit_time")
    private LocalDateTime exitTime;
    
    @Column(name = "amount_charged", precision = 10, scale = 2)
    private BigDecimal amountCharged;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookingStatus status;
    
    public enum BookingStatus {
        ACTIVE,
        COMPLETED
    }
    
    public Booking(String bookingId, ParkingSlot parkingSlot, String vehicleNumber, 
                   String phoneNumber, LocalDateTime entryTime, BookingStatus status) {
        this.bookingId = bookingId;
        this.parkingSlot = parkingSlot;
        this.vehicleNumber = vehicleNumber;
        this.phoneNumber = phoneNumber;
        this.entryTime = entryTime;
        this.status = status;
    }
}
