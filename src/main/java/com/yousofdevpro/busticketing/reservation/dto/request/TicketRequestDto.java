package com.yousofdevpro.busticketing.reservation.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TicketRequestDto {
    
    @NotBlank(message = "status is required")
    @Pattern(regexp = "UNPAID|PAID|CANCELED", message = "status must be one of UNPAID, PAID, CANCELED")
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
