package com.yousofdevpro.busticketing.reservation.dto;

import com.yousofdevpro.busticketing.core.exception.InEnum;
import com.yousofdevpro.busticketing.reservation.model.TicketStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TicketRequestDto {
    
    @NotBlank(message = "status is required")
    @InEnum(value = TicketStatus.class, message = "status must be a value in: UNPAID, PAID, CANCELED")
    private String status;
    
    @NotNull(message = "seatNumber is required")
    private Integer seatNumber;
    
    @NotNull(message = "departureDate is required")
    @FutureOrPresent(message = "departureDate must be today or in the future")
    private LocalDate departureDate;
    
    @NotNull(message = "appointmentId is required")
    private Long appointmentId;
    
    @NotNull(message = "customerUserId is required")
    private Long customerUserId;
}
