package com.parking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStats {
    private long totalSlots;
    private long availableSlots;
    private long occupiedSlots;
}
