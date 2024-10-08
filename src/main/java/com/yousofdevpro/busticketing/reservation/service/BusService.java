package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.reservation.dto.AddBusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.model.Bus;
import com.yousofdevpro.busticketing.reservation.repository.BusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BusService {
    
    private final BusRepository busRepository;
    
    public List<BusResponseDto> getBuses() {
        return busRepository.fetchBuses();
    }
    
    
    public void addBus(AddBusRequestDto addBusRequestDto) {
        var bus = Bus.builder()
                .name(addBusRequestDto.getName())
                .busNumber(addBusRequestDto.getBusNumber())
                .totalSeats(addBusRequestDto.getTotalSeats())
                .build();
        
        busRepository.save(bus);
    }
}
