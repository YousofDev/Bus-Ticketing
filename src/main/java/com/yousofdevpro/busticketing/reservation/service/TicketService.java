package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.core.exception.BadRequestException;
import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.TicketRequestDto;
import com.yousofdevpro.busticketing.reservation.model.Ticket;
import com.yousofdevpro.busticketing.reservation.model.TicketStatus;
import com.yousofdevpro.busticketing.reservation.repository.AppointmentRepository;
import com.yousofdevpro.busticketing.reservation.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TicketService {
    
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final TicketRepository ticketRepository;
    
    @Transactional
    public TicketDetailsResponseDto saveTicket(
            TicketRequestDto ticketRequestDto, Long ticketId) {
        
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
                        .orElseThrow(() -> new NotFoundException("Appointment not found"));
        
        // 4- Count the number of available tickets (seat numbers)
        int availableSeats = appointmentDto.getBusTotalSeats() - seatNumbers.size();
        System.out.println("\n" + availableSeats);
        
        if (availableSeats <= 0) {
            throw new BadRequestException("No more tickets for this appointment");
        }
        
        // 5- Ensure the appointment departure time is not passed for the today journey
        var departureDate = ticketRequestDto.getDepartureDate();
        var departureTime = appointmentDto.getDepartureTime();
        var presentDate = LocalDate.now();
        var presentTime = LocalTime.now();
        
        if (presentTime.isAfter(departureTime) && departureDate.isEqual(presentDate)) {
            throw new BadRequestException("Can't create the ticket, the today departure time is passed!");
        }
        
        // 6- Ensure the appointment is still active
        var endDate = appointmentDto.getEndDate();
        
        if (endDate!=null && endDate.isBefore(presentDate)) {
            throw new BadRequestException("Can't create a ticket for a non active appointment");
        }
        
        var appointment = appointmentRepository.getReferenceById(
                ticketRequestDto.getAppointmentId());
        
        var customer = userRepository.findById(ticketRequestDto.getCustomerUserId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        
        // 7- Persist the ticket data
        Ticket ticket;
        
        if (ticketId!=null) {
            ticket = ticketRepository.findById(ticketId).orElseThrow(() ->
                    new NotFoundException("Ticket not found!"));
        } else {
            ticket = new Ticket();
        }
        
        ticket.setStatus(TicketStatus.valueOf(ticketRequestDto.getStatus()));
        ticket.setPrice(appointmentDto.getPrice());
        ticket.setSeatNumber(ticketRequestDto.getSeatNumber());
        ticket.setDepartureDate(ticketRequestDto.getDepartureDate());
        ticket.setAppointment(appointment);
        ticket.setCustomer(customer);
        
        if (ticketRequestDto.getStatus().equals(TicketStatus.PAID.name())) {
            ticket.setPaidAt(LocalDateTime.now());
        } else if (ticketRequestDto.getStatus().equals(TicketStatus.CANCELED.name())){
            ticket.setCanceledAt(LocalDateTime.now());
        }
        
        ticket = ticketRepository.save(ticket);
        
        // TODO: Sending an email with ticket details to the customer
        
        return TicketDetailsResponseDto.builder()
                .id(ticket.getId())
                .ticketStatus(ticket.getStatus())
                .serviceGrade(appointmentDto.getServiceGrade())
                .price(ticket.getPrice())
                .seatNumber(ticket.getSeatNumber())
                .calendarDay(appointmentDto.getCalendarDay())
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
                .customerUserId(customer.getId())
                .appointmentId(appointment.getId())
                .paidAt(ticket.getPaidAt())
                .canceledAt(ticket.getCanceledAt())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .createdBy(ticket.getCreatedBy())
                .updatedBy(ticket.getUpdatedBy())
                .build();
    }
    
    public TicketDetailsResponseDto getTicketById(Long id) {
        var ticket = ticketRepository.getTicketById(id);
        
        if (ticket==null) {
            throw new NotFoundException("Ticket not found");
        }
        
        return ticket;
    }
    
    public List<TicketDetailsResponseDto> getAllTickets() {
        return ticketRepository.findAllTickets();
    }
    
    public List<TicketDetailsResponseDto> getAllTicketsByCustomerId(Long customerId) {
        return ticketRepository.findAllTicketsByCustomerId(customerId);
    }
    
    public List<TicketDetailsResponseDto> getAllTicketsByAppointmentId(Long appointmentId) {
        return ticketRepository.findAllTicketsByAppointmentId(appointmentId);
    }
    
    
    public List<TicketDetailsResponseDto> getAllValidTickets() {
        return ticketRepository.findAllValidTickets(LocalDate.now());
    }
    
    public List<TicketDetailsResponseDto> getValidTicketsByCustomerId(Long customerId) {
        return ticketRepository.findValidTicketsByCustomerId(customerId, LocalDate.now());
    }
    
    public List<TicketDetailsResponseDto> getValidTicketsByAppointmentId(Long appointmentId) {
        return ticketRepository.findValidTicketsByAppointmentId(appointmentId, LocalDate.now());
    }
    
    @Transactional
    public void payTicketById(Long id) {
        var ticket = ticketRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Ticket not found!"));
        
        if (ticket.getStatus().equals(TicketStatus.CANCELED)) {
            throw new BadRequestException("Can't pay a canceled ticket!");
        }
        
        if (ticket.getStatus().equals(TicketStatus.PAID)) {
            throw new BadRequestException("Ticket already paid!");
        }
        
        ticketRepository.payTicketById(id, LocalDateTime.now());
        
        // TODO: Sending an email with ticket status to the customer
    }
    
    @Transactional
    public void cancelTicketById(Long id) {
        var ticket = ticketRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Ticket not found"));
        
        if (ticket.getStatus().equals(TicketStatus.CANCELED)) {
            throw new BadRequestException("Ticket already canceled!");
        }
        
        ticketRepository.cancelTicketById(id, LocalDateTime.now());
        
        // TODO: Sending an email with ticket status to the customer
    }
    
}
