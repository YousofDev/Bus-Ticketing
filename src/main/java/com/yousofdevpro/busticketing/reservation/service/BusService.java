package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.config.exception.ResourceNotFoundException;
import com.yousofdevpro.busticketing.reservation.dto.AddBusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.UpdateBusRequestDto;
import com.yousofdevpro.busticketing.reservation.model.Bus;
import com.yousofdevpro.busticketing.reservation.repository.BusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class BusService {
    
    private final BusRepository busRepository;
    
    public List<BusResponseDto> getBuses() {
        return busRepository.fetchBuses();
    }
    
    
    @Transactional
    public BusResponseDto addBus(AddBusRequestDto addBusRequestDto) {
        
        var bus = Bus.builder()
                .name(addBusRequestDto.getName())
                .busNumber(addBusRequestDto.getBusNumber())
                .totalSeats(addBusRequestDto.getTotalSeats())
                .build();
        
        bus = busRepository.save(bus);
        
        return BusResponseDto.builder()
                .id(bus.getId())
                .name(bus.getName())
                .busNumber(bus.getBusNumber())
                .totalSeats(bus.getTotalSeats())
                .isActive(bus.getIsActive())
                .createdAt(bus.getCreatedAt())
                .updatedAt(bus.getUpdatedAt())
                .build();
    }
    
    
    public BusResponseDto getBusById(Long id) {
        return busRepository.fetchBusById(id).orElseThrow(() ->
                new ResourceNotFoundException("Bus not found"));
    }
    
    @Transactional
    public BusResponseDto updateBusById(UpdateBusRequestDto updateBusRequestDto, Long id) {
        var bus = busRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Bus not found"));
        
        bus.setId(id);
        bus.setName(updateBusRequestDto.getName());
        bus.setBusNumber(updateBusRequestDto.getBusNumber());
        bus.setTotalSeats(updateBusRequestDto.getTotalSeats());
        bus.setIsActive(updateBusRequestDto.getIsActive());
        
        bus = busRepository.save(bus);
        
        return BusResponseDto.builder()
                .id(bus.getId())
                .name(bus.getName())
                .busNumber(bus.getBusNumber())
                .totalSeats(bus.getTotalSeats())
                .isActive(bus.getIsActive())
                .createdAt(bus.getCreatedAt())
                .updatedAt(bus.getUpdatedAt())
                .build();
    }
    
    @Transactional
    public void deleteBusById(Long id) {
        busRepository.deleteById(id);
    }
}
