package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.config.exception.ResourceNotFoundException;
import com.yousofdevpro.busticketing.reservation.dto.AddRouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.UpdateRouteRequestDto;
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
    public RouteResponseDto addRoute(AddRouteRequestDto addRouteRequestDto) {
        
        var route = Route.builder()
                .departurePoint(addRouteRequestDto.getDeparturePoint())
                .destinationPoint(addRouteRequestDto.getDestinationPoint())
                .isActive(addRouteRequestDto.getIsActive())
                .build();
        
        route = routeRepository.save(route);
        
        return RouteResponseDto.builder()
                .id(route.getId())
                .departurePoint(route.getDeparturePoint())
                .destinationPoint(route.getDestinationPoint())
                .isActive(route.getIsActive())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }
    
    public RouteResponseDto getRouteById(Long id) {
        return routeRepository.fetchRouteById(id).orElseThrow(() ->
                new ResourceNotFoundException("Route not found"));
    }
    
    
    @Transactional
    public RouteResponseDto updateRoute(UpdateRouteRequestDto updateRouteRequestDto, Long id) {
        
        // TODO: fetch appointments by route id
        // TODO: DON'T UPDATE ROUTE: if linked with any appointments
        // TODO: UPDATE ROUTE: if not linked with any appointments
        
        var route = routeRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Route not found"));
        
        route.setDeparturePoint(updateRouteRequestDto.getDeparturePoint());
        route.setDestinationPoint(updateRouteRequestDto.getDestinationPoint());
        route.setIsActive(updateRouteRequestDto.getIsActive());
        
        route = routeRepository.save(route);
        
        return RouteResponseDto.builder()
                .id(route.getId())
                .departurePoint(route.getDeparturePoint())
                .destinationPoint(route.getDestinationPoint())
                .isActive(route.getIsActive())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }
    
    
    @Transactional
    public void deleteRouteById(Long id) {
        
        // TODO: fetch appointments by route id
        // TODO: DON'T DELETE, if linked with any appointments
        
        routeRepository.deleteById(id);
    }
    
    
}
