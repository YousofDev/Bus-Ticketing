package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.core.exception.ConflictException;
import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import com.yousofdevpro.busticketing.core.notification.EmailService;
import com.yousofdevpro.busticketing.reservation.dto.request.AppointmentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.response.AppointmentResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.AppointmentSeatsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.model.*;
import com.yousofdevpro.busticketing.reservation.repository.AppointmentRepository;
import com.yousofdevpro.busticketing.reservation.repository.BusRepository;
import com.yousofdevpro.busticketing.reservation.repository.RouteRepository;
import com.yousofdevpro.busticketing.reservation.repository.TicketRepository;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class AppointmentService {
    
    private final static Logger logger = Logger.getLogger(AppointmentService.class.getName());
    
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TicketRepository ticketRepository;
    private final EmailService emailService;
    
    /**
     * Creates a new appointment and ensures there are no conflicts in scheduling.
     */
    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {
        
        var conflictAppointments = getConflictAppointments(dto);
        if (!conflictAppointments.isEmpty()) {
            throw new ConflictException("Bus or Driver is already scheduled for another appointment at this time");
        }
        
        var appointment = Appointment.builder()
                .calendarDay(CalendarDay.valueOf(dto.getCalendarDay()))
                .serviceGrade(ServiceGrade.valueOf(dto.getServiceGrade()))
                .price(dto.getPrice())
                .departureTime(LocalTime.parse(dto.getDepartureTime()))
                .arrivalTime(LocalTime.parse(dto.getArrivalTime()))
                .effectiveDate(dto.getEffectiveDate())
                .endDate(dto.getEndDate())
                .driver(userRepository.findById(dto.getDriverUserId()).orElseThrow(() -> new NotFoundException("Driver not found")))
                .bus(busRepository.findById(dto.getBusId()).orElseThrow(() -> new NotFoundException("Bus not found")))
                .route(routeRepository.findById(dto.getRouteId()).orElseThrow(() -> new NotFoundException("Route not found")))
                .build();
        
        appointment = appointmentRepository.save(appointment);
        
        return mapToAppointmentDto(appointment);
    }
    
    /**
     * Updates an existing appointment.
     */
    @Transactional
    public AppointmentResponseDto updateAppointmentById(
            Long appointmentId, AppointmentRequestDto dto) {
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        
        List<Long> tickets = appointmentRepository.findAnyTicketByAppointmentId(appointmentId);
        
        // if the appointment not associated with any ticket, just update this appointment
        if (tickets.isEmpty()) {
            var conflictAppointments = getConflictAppointments(dto);
            if (!conflictAppointments.isEmpty()) {
                throw new ConflictException("Bus or Driver is already scheduled for another appointment at this time");
            }
            appointment.setCalendarDay(CalendarDay.valueOf(dto.getCalendarDay()));
            appointment.setServiceGrade(ServiceGrade.valueOf(dto.getServiceGrade()));
            appointment.setPrice(dto.getPrice());
            appointment.setBus(busRepository.findById(dto.getBusId()).orElseThrow(() -> new NotFoundException("Bus not found")));
            appointment.setDriver(userRepository.findById(dto.getDriverUserId()).orElseThrow(() -> new NotFoundException("Driver not found")));
            appointment.setRoute(routeRepository.findById(dto.getRouteId()).orElseThrow(() -> new NotFoundException("Route not found")));
            appointment.setDepartureTime(LocalTime.parse(dto.getDepartureTime()));
            appointment.setArrivalTime(LocalTime.parse(dto.getArrivalTime()));
            appointment.setEffectiveDate(dto.getEffectiveDate());
            appointment.setEndDate(dto.getEndDate());
            appointment = appointmentRepository.save(appointment);
            return mapToAppointmentDto(appointment);
        }
        
        // if the appointment associated with tickets:
        
        // End this appointment
        appointment.setEndDate(dto.getEffectiveDate().minusDays(1));
        appointmentRepository.save(appointment);
        
        // Create new appointment
        var conflictAppointments = getConflictAppointments(dto);
        if (!conflictAppointments.isEmpty()) {
            throw new ConflictException("Bus or Driver is already scheduled for another appointment at this time");
        }
        
        var newAppointment = Appointment.builder()
                .calendarDay(CalendarDay.valueOf(dto.getCalendarDay()))
                .serviceGrade(ServiceGrade.valueOf(dto.getServiceGrade()))
                .price(dto.getPrice())
                .departureTime(LocalTime.parse(dto.getDepartureTime()))
                .arrivalTime(LocalTime.parse(dto.getArrivalTime()))
                .effectiveDate(dto.getEffectiveDate())
                .endDate(dto.getEndDate())
                .driver(userRepository.findById(dto.getDriverUserId()).orElseThrow(() -> new NotFoundException("Driver not found")))
                .bus(busRepository.findById(dto.getBusId()).orElseThrow(() -> new NotFoundException("Bus not found")))
                .route(routeRepository.findById(dto.getRouteId()).orElseThrow(() -> new NotFoundException("Route not found")))
                .build();
        
        newAppointment = appointmentRepository.save(newAppointment);
        
        // Fetch the present tickets related to the previous appointment
        List<TicketDetailsResponseDto> presentTickets =
                ticketRepository.findValidTicketsByAppointmentId(appointmentId, LocalDate.now());
        
        // Check if there is tickets has a departure date equal or after the new appointment
        presentTickets = presentTickets.stream().filter((t) ->
                t.getDepartureDate().isEqual(dto.getEffectiveDate()) ||
                        t.getDepartureDate().isAfter(dto.getEffectiveDate())).toList();
        
        if (!presentTickets.isEmpty()) {
            // Update these tickets with the new appointment
            List<Long> ticketIds = presentTickets.stream()
                    .map(TicketDetailsResponseDto::getId).toList();
            
            ticketRepository.updateAppointmentId(appointmentId, ticketIds);
            
            // Send notifications to customers with the new appointment changes
            for (TicketDetailsResponseDto ticket : presentTickets) {
                try {
                    emailService.sendTicketMessage(ticket);
                } catch (MessagingException e) {
                    logger.warning(e.getLocalizedMessage());
                }
            }
        }
        
        return mapToAppointmentDto(newAppointment);
    }
    
    /**
     * Deletes an appointment if no tickets are associated with it.
     */
    @Transactional
    public void deleteAppointment(Long appointmentId) {
        
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        
        List<Long> tickets = appointmentRepository.findAnyTicketByAppointmentId(appointmentId);
        
        if (!tickets.isEmpty()) {
            throw new ConflictException("Cannot delete an appointment that has associated with tickets");
        }
        
        appointmentRepository.delete(appointment);
    }
    
    public List<AppointmentResponseDto> getActiveAppointments(LocalDate date) {
        return appointmentRepository.findActiveAppointments(date);
    }
    
    public List<AppointmentSeatsResponseDto> getActiveAppointmentsWithAvailableSeats(LocalDate date) {
        List<Object[]> results =
                appointmentRepository.findActiveAppointmentsWithAvailableSeats(date);
        
        return results.stream()
                .map(this::mapToAppointmentDetailsResponseDto)
                .toList();
    }
    
    private AppointmentSeatsResponseDto mapToAppointmentDetailsResponseDto(Object[] result) {
        return new AppointmentSeatsResponseDto(
                ((Number) result[0]).longValue(),                     // id
                CalendarDay.valueOf((String) result[1]),              // calendarDay
                ServiceGrade.valueOf((String) result[2]),             // serviceGrade
                (BigDecimal) result[3],                               // price
                convertToLocalTime((Time) result[4]),                 // departureTime
                convertToLocalTime((Time) result[5]),                 // arrivalTime
                ((Number) result[6]).longValue(),                     // routeId
                (String) result[7],                                   // routeDeparturePoint
                (String) result[8],                                   // routeDestinationPoint
                ((Number) result[9]).longValue(),                     // driverUserId
                (String) result[10],                                  // driverFirstName
                (String) result[11],                                  // driverLastName
                (String) result[12],                                  // driverEmail
                (String) result[13],                                  // driverPhone
                ((Number) result[14]).longValue(),                    // busId
                (String) result[15],                                  // busName
                (String) result[16],                                  // busNumber
                ((Number) result[17]).intValue(),                     // busTotalSeats
                ((Number) result[18]).intValue(),                     // busAvailableSeats
                convertToLocalDate((Date) result[19]),                // effectiveDate
                result[20]!=null ? convertToLocalDate((Date) result[20]):null, // endDate
                convertToLocalDateTime((Timestamp) result[21]),       // createdAt
                convertToLocalDateTime((Timestamp) result[22]),       // updatedAt
                result[23]!=null ? ((Number) result[23]).longValue():null, // createdBy
                result[24]!=null ? ((Number) result[24]).longValue():null  // updatedBy
        );
    }
    
    private LocalTime convertToLocalTime(Time sqlTime) {
        return sqlTime!=null ? sqlTime.toLocalTime():null;
    }
    
    private LocalDate convertToLocalDate(Date sqlDate) {
        return sqlDate!=null ? sqlDate.toLocalDate():null;
    }
    
    private LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        return timestamp!=null ? timestamp.toLocalDateTime():null;
    }
    
    /**
     * Validates if the bus or driver is already booked during the selected time slot.
     */
    public List<Appointment> getConflictAppointments(AppointmentRequestDto dto) {
        return appointmentRepository.findConflictingAppointments(
                dto.getBusId(),
                dto.getDriverUserId(),
                CalendarDay.valueOf(dto.getCalendarDay()),
                LocalTime.parse(dto.getDepartureTime()),
                LocalTime.parse(dto.getArrivalTime()));
    }
    
    
    public AppointmentResponseDto mapToAppointmentDto(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .calendarDay(appointment.getCalendarDay())
                .driverUserId(appointment.getDriver().getId())
                .busId(appointment.getBus().getId())
                .routeId(appointment.getRoute().getId())
                .serviceGrade(appointment.getServiceGrade())
                .price(appointment.getPrice())
                .departureTime(appointment.getDepartureTime())
                .arrivalTime(appointment.getArrivalTime())
                .effectiveDate(appointment.getEffectiveDate())
                .endDate(appointment.getEndDate())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .createdBy(appointment.getCreatedBy())
                .updatedBy(appointment.getUpdatedBy())
                .build();
    }
    
    
}
