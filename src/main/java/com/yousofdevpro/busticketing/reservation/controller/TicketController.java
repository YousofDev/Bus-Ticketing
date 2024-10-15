package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.TicketRequestDto;
import com.yousofdevpro.busticketing.reservation.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@AllArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @PostMapping
    public ResponseEntity<TicketDetailsResponseDto> createTicket(
            @Validated @RequestBody TicketRequestDto ticketRequestDto) {
        var createdTicket =
                ticketService.saveTicket(ticketRequestDto, null);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }
    
    @GetMapping
    public List<TicketDetailsResponseDto> getAllTickets(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size){
        // TODO: Implement the pagination
        return ticketService.getAllTickets();
    }
    
    @GetMapping("/{id}")
    public TicketDetailsResponseDto getTicketById(@PathVariable Long id){
        return ticketService.getTicketById(id);
    }
    
    @PutMapping("/{id}")
    public TicketDetailsResponseDto updateTicketById(
            @Validated @RequestBody TicketRequestDto ticketRequestDto,
            @PathVariable Long id) {
        return ticketService.saveTicket(ticketRequestDto, id);
    }
    
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelTicketById(@PathVariable Long id) {
        ticketService.cancelTicketById(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/pay")
    public ResponseEntity<Void> payTicketById(@PathVariable Long id) {
        ticketService.payTicketById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/customers/{customerId}")
    public List<TicketDetailsResponseDto> getAllTicketsByCustomerId(
            @PathVariable Long customerId) {
        return ticketService.getAllTicketsByCustomerId(customerId);
    }
    
    @GetMapping("/appointments/{appointmentId}")
    public List<TicketDetailsResponseDto> getAllTicketsByAppointmentId(
            @PathVariable Long appointmentId) {
        return ticketService.getAllTicketsByAppointmentId(appointmentId);
    }
    
    @GetMapping("/valid")
    public List<TicketDetailsResponseDto> getAllValidTickets() {
        return ticketService.getAllValidTickets();
    }
    
    @GetMapping("/customers/{customerId}/valid")
    public List<TicketDetailsResponseDto> getValidTicketsByCustomerId(
            @PathVariable Long customerId) {
        return ticketService.getValidTicketsByCustomerId(customerId);
    }
    
    @GetMapping("/appointments/{appointmentId}/valid")
    public List<TicketDetailsResponseDto> getValidTicketsByAppointmentId(
            @PathVariable Long appointmentId) {
        return ticketService.getValidTicketsByAppointmentId(appointmentId);
    }
}
