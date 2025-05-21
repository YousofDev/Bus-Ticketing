package com.yousofdevpro.busticketing.reservation.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class AppointmentRequestDto {
    
    @NotBlank(message = "calendarDay is required.")
    @Pattern(regexp = "SATURDAY|SUNDAY|MONDAY|TUESDAY|WEDNESDAY|THURSDAY|FRIDAY", message = "calendarDay must be one of SATURDAY,SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY")
    private String calendarDay;
    
    @NotBlank(message = "serviceGrade is required")
    @Pattern(regexp = "ECONOMY|BUSINESS|DELUXE", message = "serviceGrade must be one of ECONOMY, BUSINESS, DELUXE")
    private String serviceGrade;
    
    @NotNull(message = "price is required")
    private BigDecimal price;
    
    @NotBlank(message = "departureTime is required")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "departureTime must be in HH:mm format")
    private String departureTime;
    
    @NotBlank(message = "arrivalTime is required")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "arrivalTime must be in HH:mm format")
    private String arrivalTime;
    
    @NotNull(message = "effectiveDate is required")
    @FutureOrPresent(message = "effectiveDate must be today or in the future")
    private LocalDate effectiveDate;
    
    @FutureOrPresent(message = "endDate must be today or in the future if provided")
    private LocalDate endDate; // Optional
    
    @NotNull(message = "driverUserId is required")
    private Long driverUserId;
    
    @NotNull(message = "busId is required")
    private Long busId;
    
    @NotNull(message = "routeId is required")
    private Long routeId;
    
}

