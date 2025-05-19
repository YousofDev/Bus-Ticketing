package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.request.RouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.response.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.service.RouteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@AllArgsConstructor
public class RouteController {
    
    private final RouteService routeService;
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'DRIVER', 'CUSTOMER')")
    public List<RouteResponseDto> getRoutes() {
        return routeService.getRoutes();
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RouteResponseDto>addRoute(
            @Validated @RequestBody RouteRequestDto routeRequestDto){
        
        var createdRoute = routeService.addRoute(routeRequestDto);
        
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }
    
    @GetMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF', 'DRIVER', 'CUSTOMER')")
    public RouteResponseDto getRoute(@PathVariable Long id){
        return routeService.getRouteById(id);
    }
    
    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public RouteResponseDto updateRoute(
            @Validated @RequestBody RouteRequestDto routeRequestDto,
            @PathVariable Long id) {
        
        return routeService.updateRoute(routeRequestDto, id);
    }
    
    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteRoute(@PathVariable Long id) {
        routeService.deleteRouteById(id);
    }
    
    
}
