package com.yousofdevpro.busticketing.reservation.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {
    private Long id;
    private String calendarDay;
    private String serviceGrade;
    private BigDecimal price;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private Long driverUserId;
    private Long busId;
    private Long routeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
