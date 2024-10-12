package com.yousofdevpro.busticketing.reservation.repository;

import com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    @Query("SELECT t.seatNumber FROM Ticket t " +
            "WHERE t.appointment.id = :appointmentId " +
            "AND t.departureDate = :departureDate " +
            "AND t.status <> 'CANCELED'")
    List<Integer> findReservedSeatNumbers(
            @Param("appointmentId") Long appointmentId,
            @Param("departureDate") LocalDate departureDate);
    
    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.status = 'CANCELED', t.canceledOn = :date WHERE t.id = :ticketId")
    void cancelTicketById(Long ticketId, @Param("date") LocalDateTime date);
    
    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.status = 'PAID', t.paidOn = :date WHERE t.id = :ticketId")
    void payTicketById(Long ticketId, @Param("date")LocalDateTime date);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto(" +
            "t.id, t.status, a.serviceGrade, t.price, t.seatNumber, a.calendarDay, " +
            "t.departureDate, a.departureTime, a.arrivalTime, r.departurePoint, r.destinationPoint, " +
            "b.busNumber, c.firstName, c.lastName, c.email, c.phone, c.id, a.id, " +
            "t.createdAt, t.updatedAt) " +
            "FROM Ticket t " +
            "JOIN t.customer c " +
            "JOIN t.appointment a " +
            "JOIN a.route r " +
            "JOIN a.bus b " +
            "WHERE t.id = :id")
    TicketDetailsResponseDto getTicketById(@Param("id") Long id);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto(" +
            "t.id, t.status, a.serviceGrade, t.price, t.seatNumber, a.calendarDay, " +
            "t.departureDate, a.departureTime, a.arrivalTime, r.departurePoint, r.destinationPoint, " +
            "b.busNumber, c.firstName, c.lastName, c.email, c.phone, c.id, a.id, " +
            "t.createdAt, t.updatedAt) "+
            "FROM Ticket t " +
            "JOIN t.customer c " +
            "JOIN t.appointment a " +
            "JOIN a.route r " +
            "JOIN a.bus b ")
    List<TicketDetailsResponseDto>findAllTickets();
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto(" +
            "t.id, t.status, a.serviceGrade, t.price, t.seatNumber, a.calendarDay, " +
            "t.departureDate, a.departureTime, a.arrivalTime, r.departurePoint, r.destinationPoint, " +
            "b.busNumber, c.firstName, c.lastName, c.email, c.phone, c.id, a.id, " +
            "t.createdAt, t.updatedAt) " +
            "FROM Ticket t " +
            "JOIN t.customer c " +
            "JOIN t.appointment a " +
            "JOIN a.route r " +
            "JOIN a.bus b " +
            "WHERE c.id = :customerId")
    List<TicketDetailsResponseDto> findAllTicketsByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto(" +
            "t.id, t.status, a.serviceGrade, t.price, t.seatNumber, a.calendarDay, " +
            "t.departureDate, a.departureTime, a.arrivalTime, r.departurePoint, r.destinationPoint, " +
            "b.busNumber, c.firstName, c.lastName, c.email, c.phone, c.id, a.id, " +
            "t.createdAt, t.updatedAt) " +
            "FROM Ticket t " +
            "JOIN t.customer c " +
            "JOIN t.appointment a " +
            "JOIN a.route r " +
            "JOIN a.bus b " +
            "WHERE a.id = :appointmentId")
    List<TicketDetailsResponseDto> findAllTicketsByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto(" +
            "t.id, t.status, a.serviceGrade, t.price, t.seatNumber, a.calendarDay, " +
            "t.departureDate, a.departureTime, a.arrivalTime, r.departurePoint, r.destinationPoint, " +
            "b.busNumber, c.firstName, c.lastName, c.email, c.phone, c.id, a.id, " +
            "t.createdAt, t.updatedAt) " +
            "FROM Ticket t " +
            "JOIN t.customer c " +
            "JOIN t.appointment a " +
            "JOIN a.route r " +
            "JOIN a.bus b " +
            "WHERE t.departureDate >= :date")
    List<TicketDetailsResponseDto> findAllValidTickets(@Param("date") LocalDate date);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto(" +
            "t.id, t.status, a.serviceGrade, t.price, t.seatNumber, a.calendarDay, " +
            "t.departureDate, a.departureTime, a.arrivalTime, r.departurePoint, r.destinationPoint, " +
            "b.busNumber, c.firstName, c.lastName, c.email, c.phone, c.id, a.id, " +
            "t.createdAt, t.updatedAt) " +
            "FROM Ticket t " +
            "JOIN t.customer c " +
            "JOIN t.appointment a " +
            "JOIN a.route r " +
            "JOIN a.bus b " +
            "WHERE c.id = :customerId " +
            "AND t.departureDate >= :date")
    List<TicketDetailsResponseDto> findValidTicketsByCustomerId(
            @Param("customerId") Long customerId,
            @Param("date") LocalDate date);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.TicketDetailsResponseDto(" +
            "t.id, t.status, a.serviceGrade, t.price, t.seatNumber, a.calendarDay, " +
            "t.departureDate, a.departureTime, a.arrivalTime, r.departurePoint, r.destinationPoint, " +
            "b.busNumber, c.firstName, c.lastName, c.email, c.phone, c.id, a.id, " +
            "t.createdAt, t.updatedAt) " +
            "FROM Ticket t " +
            "JOIN t.customer c " +
            "JOIN t.appointment a " +
            "JOIN a.route r " +
            "JOIN a.bus b " +
            "WHERE a.id = :appointmentId " +
            "AND t.departureDate >= :date")
    List<TicketDetailsResponseDto> findValidTicketsByAppointmentId(
            @Param("appointmentId") Long appointmentId,
            @Param("date") LocalDate date);
    
}
