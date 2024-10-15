package com.yousofdevpro.busticketing.reservation.repository;

import com.yousofdevpro.busticketing.reservation.dto.AppointmentDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.AppointmentDto;
import com.yousofdevpro.busticketing.reservation.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.AppointmentDetailsResponseDto(" +
            "a.id, a.calendarDay, a.serviceGrade, a.price, a.departureTime, a.arrivalTime, r.id, "+
            "r.departurePoint, r.destinationPoint, d.id, d.firstName, d.lastName, d.email, d.phone, "+
            "b.id, b.name, b.busNumber, b.totalSeats, "+
            "a.effectiveDate, a.endDate, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy) " +
            "FROM Appointment a " +
            "JOIN a.bus b " +
            "JOIN a.route r " +
            "JOIN a.driver d " +
            "WHERE a.effectiveDate <= :date AND (a.endDate IS NULL OR a.endDate >= :date)")
    List<AppointmentDetailsResponseDto> findActiveAppointmentsDetails(@Param("date") LocalDate date);
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.AppointmentDto(" +
            "a.id, a.calendarDay, a.serviceGrade, a.price, a.departureTime, a.arrivalTime, " +
            "r.departurePoint, r.destinationPoint, " +
            "b.busNumber, b.totalSeats, a.endDate) " +
            "FROM Appointment a " +
            "JOIN a.bus b " +
            "JOIN a.route r " +
            "WHERE a.id = :id")
    Optional<AppointmentDto>findAppointmentById(@Param("id") Long id);
}
