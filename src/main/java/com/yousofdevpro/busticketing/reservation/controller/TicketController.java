package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.TicketRequestDto;
import com.yousofdevpro.busticketing.reservation.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@AllArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<TicketDetailsResponseDto> createTicket(
            @Validated @RequestBody TicketRequestDto ticketRequestDto) {
        var createdTicket =
                ticketService.saveTicket(ticketRequestDto, null);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public List<TicketDetailsResponseDto> getAllTickets(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size){
        // TODO: Implement the pagination
        return ticketService.getAllTickets();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'CUSTOMER')")
    public TicketDetailsResponseDto getTicketById(@PathVariable Long id){
        return ticketService.getTicketById(id);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public TicketDetailsResponseDto updateTicketById(
            @Validated @RequestBody TicketRequestDto ticketRequestDto,
            @PathVariable Long id) {
        return ticketService.saveTicket(ticketRequestDto, id);
    }
    
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public ResponseEntity<Void> cancelTicketById(@PathVariable Long id) {
        ticketService.cancelTicketById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<Void> payTicketById(@PathVariable Long id) {
        ticketService.payTicketById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/customers/{customerId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'CUSTOMER')")
    public List<TicketDetailsResponseDto> getAllTicketsByCustomerId(
            @PathVariable Long customerId) {
        return ticketService.getAllTicketsByCustomerId(customerId);
    }
    
    @GetMapping("/appointments/{appointmentId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public List<TicketDetailsResponseDto> getAllTicketsByAppointmentId(
            @PathVariable Long appointmentId) {
        return ticketService.getAllTicketsByAppointmentId(appointmentId);
    }
    
    @GetMapping("/valid")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public List<TicketDetailsResponseDto> getAllValidTickets() {
        return ticketService.getAllValidTickets();
    }
    
    @GetMapping("/customers/{customerId}/valid")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public List<TicketDetailsResponseDto> getValidTicketsByCustomerId(
            @PathVariable Long customerId) {
        return ticketService.getValidTicketsByCustomerId(customerId);
    }
    
    @GetMapping("/appointments/{appointmentId}/valid")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public List<TicketDetailsResponseDto> getValidTicketsByAppointmentId(
            @PathVariable Long appointmentId) {
        return ticketService.getValidTicketsByAppointmentId(appointmentId);
    }
}
