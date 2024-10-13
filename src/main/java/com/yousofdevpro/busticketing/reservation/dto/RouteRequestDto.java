package com.yousofdevpro.busticketing.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RouteRequestDto {
    
    @NotBlank(message = "departurePoint is required")
    private String departurePoint;
    
    @NotBlank(message = "destinationPoint is required")
    private String destinationPoint;
    
    @NotNull(message = "isActive is required")
    private Boolean isActive;
    
    
}
