package com.yousofdevpro.busticketing.reservation.repository;

import com.yousofdevpro.busticketing.reservation.dto.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface BusRepository extends JpaRepository<Bus, Long> {
    
    @Query("SELECT b.id AS id, b.name AS name, b.totalSeats AS total_seats FROM Bus b")
    List<BusResponseDto> fetchBuses();
}
