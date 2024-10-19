package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.core.exception.BadRequestException;
import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import com.yousofdevpro.busticketing.core.notification.EmailService;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentDto;
import com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.TicketRequestDto;
import com.yousofdevpro.busticketing.reservation.model.Ticket;
import com.yousofdevpro.busticketing.reservation.model.TicketStatus;
import com.yousofdevpro.busticketing.reservation.repository.AppointmentRepository;
import com.yousofdevpro.busticketing.reservation.repository.TicketRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class TicketService {
    
    private final static Logger logger = Logger.getLogger(TicketService.class.getName());
    
    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;
    
    @Transactional
    public TicketDetailsResponseDto saveTicket(TicketRequestDto ticketRequestDto, Long ticketId) {
        
        // 1. Get appointment details
        AppointmentDto appointmentDto =
                getAppointmentDetails(ticketRequestDto.getAppointmentId());
        
        // 2. Check ticket availability
        checkSeatAndTicketAvailability(appointmentDto, ticketRequestDto);
        
        // 3. Validate departure time and appointment active status
        validateDepartureTimeAndAppointmentStatus(appointmentDto, ticketRequestDto.getDepartureDate());
        
        // 4. Get or create ticket
        Ticket ticket = getOrCreateTicket(ticketId);
        
        // 5. Update ticket details
        updateTicketDetails(ticket, ticketRequestDto, appointmentDto);
        
        // 6. Save ticket
        ticket = ticketRepository.save(ticket);
        
        // 7. Create ticket details response
        TicketDetailsResponseDto ticketDetails =
                createTicketDetailsResponse(ticket, appointmentDto);
        
        // 8. Send email notification
        sendTicketMessage(ticketDetails);
        
        return ticketDetails;
    }
    
    private void checkSeatAndTicketAvailability(
            AppointmentDto appointmentDto, TicketRequestDto ticketRequestDto) {
        List<Integer> reservedSeatNumbers = ticketRepository.findReservedSeatNumbers(
                ticketRequestDto.getAppointmentId(),
                ticketRequestDto.getDepartureDate());
        
        // Check if the requested seat is already taken
        if (reservedSeatNumbers.contains(ticketRequestDto.getSeatNumber())) {
            throw new BadRequestException(
                    "Seat number " + ticketRequestDto.getSeatNumber() + " is already taken!");
        }
        
        // Check if there are any available seats
        int totalSeats = appointmentDto.getBusTotalSeats();
        int reservedSeats = reservedSeatNumbers.size();
        
        if (reservedSeats >= totalSeats) {
            throw new BadRequestException(
                    "No more tickets available for this appointment. All "
                            + totalSeats + " seats are taken.");
        }
        
        // Check if the requested seat number is valid
        if (ticketRequestDto.getSeatNumber() <= 0 ||
                ticketRequestDto.getSeatNumber() > totalSeats) {
            throw new BadRequestException(
                    "Invalid seat number. Please choose a seat between 1 and " + totalSeats);
        }
    }
    
    private AppointmentDto getAppointmentDetails(Long appointmentId) {
        return appointmentRepository.findAppointmentById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
    }
    
    private void validateDepartureTimeAndAppointmentStatus(
            AppointmentDto appointmentDto, LocalDate departureDate) {
        LocalDate presentDate = LocalDate.now();
        LocalTime presentTime = LocalTime.now();
        
        if (departureDate.isEqual(presentDate) &&
                presentTime.isAfter(appointmentDto.getDepartureTime())) {
            throw new BadRequestException("Can't create the ticket, today's departure time has passed!");
        }
        
        LocalDate endDate = appointmentDto.getEndDate();
        if (endDate!=null && endDate.isBefore(presentDate)) {
            throw new BadRequestException("Can't create a ticket for an inactive appointment");
        }
    }
    
    private Ticket getOrCreateTicket(Long ticketId) {
        if (ticketId!=null) {
            return ticketRepository.findById(ticketId)
                    .orElseThrow(() -> new NotFoundException("Ticket not found!"));
        }
        return new Ticket();
    }
    
    private void updateTicketDetails(Ticket ticket,
                                     TicketRequestDto ticketRequestDto,
                                     AppointmentDto appointmentDto) {
        ticket.setStatus(TicketStatus.valueOf(ticketRequestDto.getStatus()));
        ticket.setPrice(appointmentDto.getPrice());
        ticket.setSeatNumber(ticketRequestDto.getSeatNumber());
        ticket.setDepartureDate(ticketRequestDto.getDepartureDate());
        ticket.setAppointment(appointmentRepository.getReferenceById(ticketRequestDto.getAppointmentId()));
        ticket.setCustomer(userRepository.findById(ticketRequestDto.getCustomerUserId())
                .orElseThrow(() -> new NotFoundException("Customer not found")));
        
        if (TicketStatus.PAID.name().equals(ticketRequestDto.getStatus())) {
            ticket.setPaidAt(LocalDateTime.now());
        } else if (TicketStatus.CANCELED.name().equals(ticketRequestDto.getStatus())) {
            ticket.setCanceledAt(LocalDateTime.now());
        }
    }
    
    private TicketDetailsResponseDto createTicketDetailsResponse(
            Ticket ticket,
            AppointmentDto appointmentDto) {
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
                .customerFirstName(ticket.getCustomer().getFirstName())
                .customerLastName(ticket.getCustomer().getLastName())
                .customerEmail(ticket.getCustomer().getEmail())
                .customerPhone(ticket.getCustomer().getPhone())
                .customerUserId(ticket.getCustomer().getId())
                .appointmentId(ticket.getAppointment().getId())
                .paidAt(ticket.getPaidAt())
                .canceledAt(ticket.getCanceledAt())
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .createdBy(ticket.getCreatedBy())
                .updatedBy(ticket.getUpdatedBy())
                .build();
    }
    
    public TicketDetailsResponseDto getTicketById(Long id) {
        return ticketRepository.getTicketById(id)
                .orElseThrow(()->new NotFoundException("Ticket not found"));
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
        var ticket = ticketRepository.getTicketById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));
        
        if (ticket.getTicketStatus().equals(TicketStatus.CANCELED)) {
            throw new BadRequestException("Can't pay a canceled ticket!");
        }
        
        if (ticket.getTicketStatus().equals(TicketStatus.PAID)) {
            throw new BadRequestException("Ticket already paid!");
        }
        
        ticketRepository.payTicketById(id, LocalDateTime.now());
        
        ticket.setTicketStatus(TicketStatus.PAID);
        
        // Sending an email with the new ticket details to the customer
        sendTicketMessage(ticket);
    }
    
    @Transactional
    public void cancelTicketById(Long id) {
        var ticket = ticketRepository.getTicketById(id)
                .orElseThrow(() -> new NotFoundException("Ticket not found"));
        
        if (ticket.getTicketStatus().equals(TicketStatus.CANCELED)) {
            throw new BadRequestException("Ticket already canceled!");
        }
        
        ticketRepository.cancelTicketById(id, LocalDateTime.now());
        
        ticket.setTicketStatus(TicketStatus.CANCELED);
        
        // Sending an email with the new ticket details to the customer
        sendTicketMessage(ticket);
    }
    
    private void sendTicketMessage(TicketDetailsResponseDto ticket) {
        try {
            emailService.sendTicketMessage(ticket);
        } catch (MessagingException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }
    
}
