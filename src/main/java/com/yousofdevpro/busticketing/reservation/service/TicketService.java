package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.config.exception.BadRequestException;
import com.yousofdevpro.busticketing.config.exception.ResourceNotFoundException;
import com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.TicketRequestDto;
import com.yousofdevpro.busticketing.reservation.model.Payment;
import com.yousofdevpro.busticketing.reservation.model.PaymentStatus;
import com.yousofdevpro.busticketing.reservation.model.Ticket;
import com.yousofdevpro.busticketing.reservation.model.TicketStatus;
import com.yousofdevpro.busticketing.reservation.repository.AppointmentRepository;
import com.yousofdevpro.busticketing.reservation.repository.PaymentRepository;
import com.yousofdevpro.busticketing.reservation.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {
    
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    
    
    @Transactional
    public TicketDetailsResponseDto createTicket(TicketRequestDto ticketRequestDto) {
        
        // 1 - Fetch all seat numbers for the given appointment and date
        List<Integer> seatNumbers = ticketRepository.findReservedSeatNumbers(
                ticketRequestDto.getAppointmentId(),
                ticketRequestDto.getDepartureDate());
        
        // 2- Check if the specified seat number exists in the list
        if (seatNumbers.contains(ticketRequestDto.getSeatNumber())) {
            throw new BadRequestException("Seat number already taken!");
        }
        
        // 3- Get the appointment details
        var appointmentDto =
                appointmentRepository.findAppointmentById(ticketRequestDto.getAppointmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        // 4- Count the number of available tickets (seat numbers)
        int availableSeats = appointmentDto.getBusTotalSeats() - seatNumbers.size();
        System.out.println("\n" + availableSeats);
        
        if (availableSeats <= 0) {
            throw new BadRequestException("No more tickets for this appointment");
        }
        
        // 5- Ensure the appointment is still active
        var endDate = appointmentDto.getEndDate();
        var present = LocalDate.now();
        
        if (endDate!=null && endDate.isBefore(present)) {
            throw new BadRequestException("Can't create ticket for a non active appointment");
        }
        
        var appointment = appointmentRepository.getReferenceById(
                ticketRequestDto.getAppointmentId());
        
        var customer = userRepository.findById(ticketRequestDto.getCustomerUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        // 6- Persist the ticket data
        var ticket = Ticket.builder()
                .price(appointmentDto.getPrice())
                .seatNumber(ticketRequestDto.getSeatNumber())
                .departureDate(ticketRequestDto.getDepartureDate())
                .appointment(appointment)
                .customer(customer)
                .build();
        
        if (ticketRequestDto.getIsPaid()) {
            ticket.setStatus(TicketStatus.COMPLETED);
        } else {
            ticket.setStatus(TicketStatus.BOOKED);
        }
        
        ticket = ticketRepository.save(ticket);
        
        if (ticketRequestDto.getIsPaid()) {
            // Issue a completed payment
            var payment = Payment.builder()
                    .status(PaymentStatus.COMPLETED)
                    .amount(ticket.getPrice())
                    .ticket(ticket)
                    .customer(customer)
                    .description("Payment completed successfully")
                    .build();
            
            paymentRepository.save(payment);
        }
        
        return TicketDetailsResponseDto.builder()
                .id(ticket.getId())
                .status(ticket.getStatus())
                .price(ticket.getPrice())
                .seatNumber(ticket.getSeatNumber())
                .departureDate(ticket.getDepartureDate())
                .departureTime(appointmentDto.getDepartureTime())
                .arrivalTime(appointmentDto.getArrivalTime())
                .departurePoint(appointmentDto.getDeparturePoint())
                .destinationPoint(appointmentDto.getDestinationPoint())
                .busNumber(appointmentDto.getBusNumber())
                .customerFirstName(customer.getFirstName())
                .customerLastName(customer.getLastName())
                .customerEmail(customer.getEmail())
                .customerPhone(customer.getPhone())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }
    
    
}
