package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.AddBusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.service.BusService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void addBus(AddBusRequestDto addBusRequestDto){
        busService.addBus(addBusRequestDto);
    }
    
}
