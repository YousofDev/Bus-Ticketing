package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.RouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.service.RouteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@AllArgsConstructor
public class RouteController {
    
    private final RouteService routeService;
    
    @GetMapping
    public List<RouteResponseDto> getRoutes() {
        return routeService.getRoutes();
    }
    
    @PostMapping
    public ResponseEntity<RouteResponseDto>addRoute(
            @Validated @RequestBody RouteRequestDto routeRequestDto){
        
        var createdRoute = routeService.addRoute(routeRequestDto);
        
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }
    
    @GetMapping("{id}")
    public RouteResponseDto getRoute(@PathVariable("id") Long id){
        return routeService.getRouteById(id);
    }
    
    @PutMapping("{id}")
    public RouteResponseDto updateRoute(
            @Validated @RequestBody RouteRequestDto routeRequestDto,
            @PathVariable("id") Long id) {
        
        return routeService.updateRoute(routeRequestDto, id);
    }
    
    @DeleteMapping("{id}")
    public void deleteRoute(@PathVariable("id") Long id) {
        routeService.deleteRouteById(id);
    }
    
    
}
