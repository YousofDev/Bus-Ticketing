package com.yousofdevpro.busticketing.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RouteRequestDto {
    
    @NotBlank(message = "Departure point is required")
    private String departurePoint;
    
    @NotBlank(message = "Destination point is required")
    private String destinationPoint;
    
    @NotNull(message = "Is active is required")
    private Boolean isActive;
    
    
}
