package com.yousofdevpro.busticketing.reservation.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class BusRequestDto {
    
    @NotBlank(message = "Bus name is required")
    private String name;
    
    @NotBlank(message = "Bus number is required")
    private String busNumber;
    
    @NotNull(message = "Total seats cannot be null")
    @Min(value = 1, message = "Total seats must be at least 1")
    private Integer totalSeats;
    
    @NotNull(message = "Is active is required")
    private Boolean isActive;
}
