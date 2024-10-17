package com.yousofdevpro.busticketing.reservation.repository;

import com.yousofdevpro.busticketing.reservation.dto.AppointmentResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentSimpleDto;
import com.yousofdevpro.busticketing.reservation.model.Appointment;
import com.yousofdevpro.busticketing.reservation.model.CalendarDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.AppointmentSimpleDto(" +
            "a.id, a.calendarDay, a.serviceGrade, a.price, a.departureTime, a.arrivalTime, " +
            "r.departurePoint, r.destinationPoint, " +
            "b.busNumber, b.totalSeats, a.endDate) " +
            "FROM Appointment a " +
            "JOIN a.bus b " +
            "JOIN a.route r " +
            "WHERE a.id = :id")
    Optional<AppointmentSimpleDto> findAppointmentById(@Param("id") Long id);
    
    @Query("SELECT a FROM Appointment a " +
            "WHERE a.calendarDay = :calendarDay " +
            "AND (a.bus.id = :busId OR a.driver.id = :driverUserId) " +
            "AND ((:departureTime < a.arrivalTime AND :arrivalTime > a.departureTime) " +
            "OR (:departureTime = a.departureTime))")
    List<Appointment> findConflictingAppointments(
            @Param("busId") Long busId,
            @Param("driverUserId") Long driverUserId,
            @Param("calendarDay") CalendarDay calendarDay,
            @Param("departureTime") LocalTime departureTime,
            @Param("arrivalTime") LocalTime arrivalTime);
    
    @Query("SELECT t.id " +
            "FROM Ticket t " +
            "JOIN t.appointment a " +
            "WHERE a.id = :appointmentId")
    List<Long> findAnyTicketByAppointmentId(@Param("appointmentId") Long appointmentId);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.AppointmentResponseDto(" +
            "a.id, a.calendarDay, a.serviceGrade, a.price, a.departureTime, a.arrivalTime, r.id, " +
            "r.departurePoint, r.destinationPoint, d.id, d.firstName, d.lastName, d.email, d.phone, " +
            "b.id, b.name, b.busNumber, b.totalSeats, " +
            "a.effectiveDate, a.endDate, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy) " +
            "FROM Appointment a " +
            "JOIN a.bus b " +
            "JOIN a.route r " +
            "JOIN a.driver d " +
            "WHERE a.effectiveDate <= :date AND (a.endDate IS NULL OR a.endDate >= :date)")
    List<AppointmentResponseDto> findActiveAppointments(@Param("date") LocalDate date);
    
    @Query(value = "SELECT a.id, a.calendar_day, a.service_grade, a.price, a.departure_time, a.arrival_time, " +
            "r.id AS routeId, r.departure_point, r.destination_point, u.id AS driverId, u.first_name, u.last_name, u.email, u.phone, " +
            "b.id AS busId, b.name, b.bus_number, b.total_seats, " +
            "(b.total_seats - (SELECT COUNT(t.id) FROM ticket t WHERE t.appointment_id = a.id AND t.status <> 'CANCELED')) AS available_seats, " +
            "a.effective_date, a.end_date, a.created_at, a.updated_at, a.created_by, a.updated_by " +
            "FROM appointment a " +
            "JOIN bus b ON a.bus_id = b.id " +
            "JOIN route r ON a.route_id = r.id " +
            "JOIN users u ON a.driver_user_id = u.id " +
            "WHERE a.effective_date <= :date AND (a.end_date IS NULL OR a.end_date >= :date)",
            nativeQuery = true)
    List<Object[]> findActiveAppointmentsWithAvailableSeats(@Param("date") LocalDate date);
    
    
    
}
