package com.parking.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "parking_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSlot {
    
    @Id
    @Column(name = "slot_id")
    private String slotId;
    
    @Column(name = "floor", nullable = false)
    private String floor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SlotStatus status;
    
    public enum SlotStatus {
        EMPTY,
        OCCUPIED
    }
}
