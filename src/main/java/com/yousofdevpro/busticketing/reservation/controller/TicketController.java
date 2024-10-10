package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.TicketRequestDto;
import com.yousofdevpro.busticketing.reservation.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tickets")
@AllArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @PostMapping
    public ResponseEntity<TicketDetailsResponseDto> createTicket(
            @Validated @RequestBody TicketRequestDto ticketRequestDto) {
        
        var createdTicket = ticketService.createTicket(ticketRequestDto);
        
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }
}
