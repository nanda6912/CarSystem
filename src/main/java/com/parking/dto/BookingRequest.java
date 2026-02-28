package com.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
