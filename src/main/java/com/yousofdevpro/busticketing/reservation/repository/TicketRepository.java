package com.yousofdevpro.busticketing.reservation.repository;

import com.yousofdevpro.busticketing.reservation.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    Integer countByAppointmentIdAndDepartureDate(Long appointmentId, LocalDate departureDate);
    
    @Query("SELECT t.seatNumber FROM Ticket t " +
            "WHERE t.appointment.id = :appointmentId " +
            "AND t.departureDate = :departureDate")
    List<Integer> findReservedSeatNumbers(@Param("appointmentId") Long appointmentId,
                                  @Param("departureDate") LocalDate departureDate);

}
