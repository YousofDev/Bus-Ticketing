package com.yousofdevpro.busticketing.reservation.repository;

import com.yousofdevpro.busticketing.reservation.dto.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface RouteRepository extends JpaRepository<Route, Long> {
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.RouteResponseDto(" +
            "r.id, r.departurePoint, r.destinationPoint, r.isActive, " +
            "r.createdAt, r.updatedAt, r.createdBy, r.updatedBy) " +
            "FROM Route r")
    List<RouteResponseDto>fetchRoutes();
    
    @Query("SELECT new com.yousofdevpro.busticketing.reservation.dto.RouteResponseDto(" +
            "r.id, r.departurePoint, r.destinationPoint, r.isActive, " +
            "r.createdAt, r.updatedAt, r.createdBy, r.updatedBy) " +
            "FROM Route r WHERE id = :id")
    Optional<RouteResponseDto>fetchRouteById(@Param("id") Long id);
    
    

}
