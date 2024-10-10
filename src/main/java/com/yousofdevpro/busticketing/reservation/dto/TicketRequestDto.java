package com.yousofdevpro.busticketing.reservation.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TicketRequestDto {
    
    @NotNull(message = "Seat number is required")
    private Integer seatNumber;
    
    @NotNull(message = "Departure date is required")
    @FutureOrPresent(message = "Departure date must be today or in the future")
    private LocalDate departureDate;
    
    @NotNull(message = "Appointment id is required")
    private Long appointmentId;
    
    @NotNull(message = "Customer user id required")
    private Long customerUserId;
    
    @NotNull(message = "isPaid can't be null")
    private Boolean isPaid;
}
