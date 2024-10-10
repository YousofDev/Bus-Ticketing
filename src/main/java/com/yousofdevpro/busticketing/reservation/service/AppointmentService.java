package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentResponseDto;
import com.yousofdevpro.busticketing.reservation.model.*;
import com.yousofdevpro.busticketing.reservation.repository.AppointmentRepository;
import com.yousofdevpro.busticketing.reservation.repository.BusRepository;
import com.yousofdevpro.busticketing.reservation.repository.RouteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    
    /*
       # Appointment Rules:
       --------------------
       -can't create an appointment for these cases:
        if there is another appointment with same bus and same departure time.
        or the arrival time for another bus appointment is after the new departure time
       
    */
    
    @Transactional
    public AppointmentResponseDto addAppointment(AppointmentRequestDto appointmentDto) {
        
        // TODO: Check if there is another appointment with the same driver, bus, departure time
        
        
        var driver = userRepository.getReferenceById(appointmentDto.getDriverUserId());
        var bus = busRepository.getReferenceById(appointmentDto.getBusId());
        var route = routeRepository.getReferenceById(appointmentDto.getRouteId());
        
        var appointment = Appointment.builder()
                .calendarDay(CalendarDay.valueOf(appointmentDto.getCalendarDay()))
                .serviceGrade(ServiceGrade.valueOf(appointmentDto.getServiceGrade()))
                .price(appointmentDto.getPrice())
                .departureTime(LocalTime.parse(appointmentDto.getDepartureTime()))
                .arrivalTime(LocalTime.parse(appointmentDto.getArrivalTime()))
                .effectiveDate(appointmentDto.getEffectiveDate())
                .endDate(appointmentDto.getEndDate())
                .driver(driver)
                .bus(bus)
                .route(route)
                .build();
        
        appointment = appointmentRepository.save(appointment);
        
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .calendarDay(appointment.getCalendarDay().name())
                .driverUserId(appointmentDto.getDriverUserId())
                .busId(appointmentDto.getBusId())
                .routeId(appointmentDto.getRouteId())
                .serviceGrade(appointment.getServiceGrade().name())
                .price(appointment.getPrice())
                .departureTime(appointment.getDepartureTime())
                .arrivalTime(appointment.getArrivalTime())
                .effectiveDate(appointment.getEffectiveDate())
                .endDate(appointment.getEndDate())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
    
    // public AppointmentDetailsResponseDto getAppointmentById(Long id) {
    //     return appointmentRepository.findAppointmentDetailsById(id).orElseThrow(() ->
    //             new ResourceNotFoundException("Appointment not found"));
    // }
    
    
    public List<AppointmentDetailsResponseDto> getActiveAppointmentsDetails(LocalDate date) {
        
        // TODO: Should also include the bus available seats
        //  based on total reserved tickets for the same appointment
        
        return appointmentRepository.findActiveAppointmentsDetails(date);
    }
    
    
}
