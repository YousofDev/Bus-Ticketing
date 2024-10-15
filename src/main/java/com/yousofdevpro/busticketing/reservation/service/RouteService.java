package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import com.yousofdevpro.busticketing.reservation.dto.RouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.model.Route;
import com.yousofdevpro.busticketing.reservation.repository.RouteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class RouteService {
    
    private final RouteRepository routeRepository;
    
    public List<RouteResponseDto> getRoutes(){
        return routeRepository.fetchRoutes();
    }
    
    @Transactional
    public RouteResponseDto addRoute(RouteRequestDto routeRequestDto) {
        
        var route = Route.builder()
                .departurePoint(routeRequestDto.getDeparturePoint())
                .destinationPoint(routeRequestDto.getDestinationPoint())
                .isActive(routeRequestDto.getIsActive())
                .build();
        
        route = routeRepository.save(route);
        
        return RouteResponseDto.builder()
                .id(route.getId())
                .departurePoint(route.getDeparturePoint())
                .destinationPoint(route.getDestinationPoint())
                .isActive(route.getIsActive())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .createdBy(route.getCreatedBy())
                .updatedBy(route.getUpdatedBy())
                .build();
    }
    
    public RouteResponseDto getRouteById(Long id) {
        return routeRepository.fetchRouteById(id).orElseThrow(() ->
                new NotFoundException("Route not found"));
    }
    
    
    @Transactional
    public RouteResponseDto updateRoute(RouteRequestDto routeRequestDto, Long id) {
        
        // TODO: fetch appointments by route id
        // TODO: DON'T UPDATE ROUTE: if linked with any appointments
        // TODO: UPDATE ROUTE: if not linked with any appointments
        
        var route = routeRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Route not found"));
        
        route.setDeparturePoint(routeRequestDto.getDeparturePoint());
        route.setDestinationPoint(routeRequestDto.getDestinationPoint());
        route.setIsActive(routeRequestDto.getIsActive());
        
        route = routeRepository.save(route);
        
        return RouteResponseDto.builder()
                .id(route.getId())
                .departurePoint(route.getDeparturePoint())
                .destinationPoint(route.getDestinationPoint())
                .isActive(route.getIsActive())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .createdBy(route.getCreatedBy())
                .updatedBy(route.getUpdatedBy())
                .build();
    }
    
    
    @Transactional
    public void deleteRouteById(Long id) {
        
        // TODO: fetch appointments by route id
        // TODO: DON'T DELETE, if linked with any appointments
        
        routeRepository.deleteById(id);
    }
    
    
}
