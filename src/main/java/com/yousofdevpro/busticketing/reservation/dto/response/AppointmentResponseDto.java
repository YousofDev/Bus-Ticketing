package com.yousofdevpro.busticketing.reservation.dto.response;

import com.yousofdevpro.busticketing.reservation.model.CalendarDay;
import com.yousofdevpro.busticketing.reservation.model.ServiceGrade;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentResponseDto {
    private Long id;
    private CalendarDay calendarDay;
    private ServiceGrade serviceGrade;
    private BigDecimal price;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private Long routeId;
    private String routeDeparturePoint;
    private String routeDestinationPoint;
    private Long driverUserId;
    private String driverFirstName;
    private String driverLastName;
    private String driverEmail;
    private String driverPhone;
    private Long busId;
    private String busName;
    private String busNumber;
    private Integer busTotalSeats;
    private LocalDate effectiveDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
