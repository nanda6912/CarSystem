package com.parking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParkingSlotDTO {
    private String slotId;
    private String floor;
    private String status;
    
    public static ParkingSlotDTO fromEntity(com.parking.entity.ParkingSlot slot) {
        return new ParkingSlotDTO(
            slot.getSlotId(),
            slot.getFloor(),
            slot.getStatus().toString()
        );
    }
}
