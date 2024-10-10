package com.yousofdevpro.busticketing.reservation.dto;

import com.yousofdevpro.busticketing.reservation.model.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TicketDetailsResponseDto {
    private Long id;
    private TicketStatus status;
    private BigDecimal price;
    private Integer seatNumber;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String departurePoint;
    private String destinationPoint;
    private String busNumber;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
