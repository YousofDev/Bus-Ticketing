package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.AddRouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.UpdateRouteRequestDto;
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
            @Validated @RequestBody AddRouteRequestDto addRouteRequestDto){
        
        var createdRoute = routeService.addRoute(addRouteRequestDto);
        
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }
    
    @GetMapping("{id}")
    public RouteResponseDto getRoute(@PathVariable("id") Long id){
        return routeService.getRouteById(id);
    }
    
    @PutMapping("{id}")
    public RouteResponseDto updateRoute(
            @Validated @RequestBody UpdateRouteRequestDto updateRouteRequestDto,
            @PathVariable("id") Long id) {
        
        return routeService.updateRoute(updateRouteRequestDto, id);
    }
    
    @DeleteMapping("{id}")
    public void deleteRoute(@PathVariable("id") Long id) {
        routeService.deleteRouteById(id);
    }
    
    
}
