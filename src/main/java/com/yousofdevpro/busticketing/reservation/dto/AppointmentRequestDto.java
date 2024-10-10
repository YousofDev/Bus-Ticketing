package com.yousofdevpro.busticketing.reservation.dto;

import com.yousofdevpro.busticketing.config.exception.InEnum;
import com.yousofdevpro.busticketing.reservation.model.CalendarDay;
import com.yousofdevpro.busticketing.reservation.model.ServiceGrade;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class AppointmentRequestDto {
    
    @NotBlank(message = "Calendar day is required.")
    @InEnum(value = CalendarDay.class, message = "Calendar day must be a value in: SATURDAY,SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY")
    private String calendarDay;
    
    @NotBlank(message = "Service grade is required")
    @InEnum(value = ServiceGrade.class, message = "Service grade must be a value in: ECONOMY, BUSINESS, DELUXE")
    private String serviceGrade;
    
    @NotNull(message = "Price is required")
    private BigDecimal price;
    
    @NotBlank(message = "Departure time cannot be null")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Departure time must be in HH:mm format")
    private String departureTime;
    
    @NotBlank(message = "Arrival time cannot be null")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Arrival time must be in HH:mm format")
    private String arrivalTime;
    
    @NotNull(message = "Effective date cannot be null")
    @FutureOrPresent(message = "Effective date must be today or in the future")
    private LocalDate effectiveDate;
    
    @FutureOrPresent(message = "End date must be today or in the future if provided")
    private LocalDate endDate; // Optional
    
    @NotNull(message = "Driver user id is required")
    private Long driverUserId;
    
    @NotNull(message = "Bus id is required")
    private Long busId;
    
    @NotNull(message = "Route id is required")
    private Long routeId;
    
}

