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
    
    @NotBlank(message = "calendarDay is required.")
    @InEnum(value = CalendarDay.class, message = "calendarDay must be a value in: SATURDAY,SUNDAY,MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY")
    private String calendarDay;
    
    @NotBlank(message = "serviceGrade is required")
    @InEnum(value = ServiceGrade.class, message = "serviceGrade must be a value in: ECONOMY, BUSINESS, DELUXE")
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

