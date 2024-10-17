package com.yousofdevpro.busticketing.reservation.dto;

import com.yousofdevpro.busticketing.reservation.model.CalendarDay;
import com.yousofdevpro.busticketing.reservation.model.ServiceGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentSimpleDto {
    private Long id;
    private CalendarDay calendarDay;
    private ServiceGrade serviceGrade;
    private BigDecimal price;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String departurePoint;
    private String destinationPoint;
    private String busNumber;
    private Integer busTotalSeats;
    private LocalDate endDate;
}
