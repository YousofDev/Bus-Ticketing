package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.AppointmentDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentResponseDto;
import com.yousofdevpro.busticketing.reservation.service.AppointmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@AllArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;
    
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> addAppointment(@Validated @RequestBody AppointmentRequestDto appointmentRequestDto) {
        
        var createdAppointment = appointmentService.addAppointment(appointmentRequestDto);
        
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }
    
    
    @GetMapping
    public List<AppointmentDetailsResponseDto> getActiveAppointmentsDetails() {
        return appointmentService.getActiveAppointmentsDetails(LocalDate.now());
    }
    
    // @GetMapping("/{id}")
    // public AppointmentDetailsResponseDto getAppointmentDetails(@PathVariable("id") Long id) {
    //     return appointmentService.getAppointmentDetails(id);
    // }
    
}
