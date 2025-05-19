package com.yousofdevpro.busticketing.reservation.repository;

import com.yousofdevpro.busticketing.reservation.dto.response.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BusRepository extends JpaRepository<Bus, Long> {
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.response.BusResponseDto(" +
            "b.id, b.busNumber, b.name, b.totalSeats, b.isActive, " +
            "b.createdAt, b.updatedAt, b.createdBy, b.updatedBy) " +
            "FROM Bus b")
    List<BusResponseDto> fetchBuses();
    
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.response.BusResponseDto(" +
            "b.id, b.busNumber, b.name, b.totalSeats, b.isActive, " +
            "b.createdAt, b.updatedAt, b.createdBy, b.updatedBy) " +
            "FROM Bus b WHERE b.id = :id")
    Optional<BusResponseDto> fetchBusById(@Param("id") Long id);
}
