package com.yousofdevpro.busticketing.reservation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BusRequestDto {
    
    @NotBlank(message = "name is required")
    private String name;
    
    @NotBlank(message = "busNumber is required")
    private String busNumber;
    
    @NotNull(message = "totalSeats is required")
    @Min(value = 1, message = "totalSeats must be at least 1")
    private Integer totalSeats;
    
    @NotNull(message = "isActive is required")
    private Boolean isActive;
}
