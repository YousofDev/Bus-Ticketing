package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.BusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.service.BusService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/buses")
@AllArgsConstructor
public class BusController {
    
    private final BusService busService;
    
    @GetMapping
    public List<BusResponseDto> getBuses(){
        return busService.getBuses();
    }
    
    @PostMapping
    public ResponseEntity<BusResponseDto> addBus(
            @Validated @RequestBody BusRequestDto busRequestDto){
        var createdBus = busService.addBus(busRequestDto);
        return new ResponseEntity<>(createdBus, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public BusResponseDto getBus(@PathVariable Long id) {
        return busService.getBusById(id);
    }
    
    @PutMapping("/{id}")
    public BusResponseDto updateBus(
            @Validated @RequestBody BusRequestDto busRequestDto,
            @PathVariable Long id) {
        return busService.updateBusById(busRequestDto, id);
    }
    
    @DeleteMapping("/{id}")
    public void deleteBus(@PathVariable Long id) {
        busService.deleteBusById(id);
    }
    
    
    
}
