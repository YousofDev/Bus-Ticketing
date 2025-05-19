package com.yousofdevpro.busticketing.reservation.dto.response;

import com.yousofdevpro.busticketing.reservation.model.CalendarDay;
import com.yousofdevpro.busticketing.reservation.model.ServiceGrade;
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
    private TicketStatus ticketStatus;
    private ServiceGrade serviceGrade;
    private BigDecimal price;
    private Integer seatNumber;
    private CalendarDay calendarDay;
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
    private Long customerUserId;
    private Long appointmentId;
    private LocalDateTime paidAt;
    private LocalDateTime canceledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
