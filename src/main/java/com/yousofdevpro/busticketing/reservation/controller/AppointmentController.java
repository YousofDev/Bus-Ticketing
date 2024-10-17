package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.AppointmentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentSeatsResponseDto;
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
    
    @GetMapping
    public List<AppointmentSeatsResponseDto> getActiveAppointmentsWithSeats() {
        return appointmentService.getActiveAppointmentsWithAvailableSeats(LocalDate.now());
    }
    
    @PostMapping
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @Validated @RequestBody AppointmentRequestDto appointmentRequestDto) {
        
        var createdAppointment =
                appointmentService.createAppointment(appointmentRequestDto);
        
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(
            @PathVariable("id") Long id,
            @Validated @RequestBody AppointmentRequestDto appointmentRequestDto) {
        
        var updatedAppointment =
                appointmentService.updateAppointmentById(id, appointmentRequestDto);
        
        return ResponseEntity.ok(updatedAppointment);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable("id") Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
    
}
