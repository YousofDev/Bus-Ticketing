package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import com.yousofdevpro.busticketing.reservation.dto.BusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.BusResponseDto;
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
    public BusResponseDto addBus(BusRequestDto busRequestDto) {
        
        var bus = Bus.builder()
                .name(busRequestDto.getName())
                .busNumber(busRequestDto.getBusNumber())
                .totalSeats(busRequestDto.getTotalSeats())
                .isActive(busRequestDto.getIsActive())
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
                .createdBy(bus.getCreatedBy())
                .updatedBy(bus.getUpdatedBy())
                .build();
    }
    
    
    public BusResponseDto getBusById(Long id) {
        return busRepository.fetchBusById(id).orElseThrow(() ->
                new NotFoundException("Bus not found"));
    }
    
    @Transactional
    public BusResponseDto updateBusById(BusRequestDto busRequestDto, Long id) {
        var bus = busRepository.findById(id).orElseThrow(()->
                new NotFoundException("Bus not found"));
        
        bus.setId(id);
        bus.setName(busRequestDto.getName());
        bus.setBusNumber(busRequestDto.getBusNumber());
        bus.setTotalSeats(busRequestDto.getTotalSeats());
        bus.setIsActive(busRequestDto.getIsActive());
        
        bus = busRepository.save(bus);
        
        return BusResponseDto.builder()
                .id(bus.getId())
                .name(bus.getName())
                .busNumber(bus.getBusNumber())
                .totalSeats(bus.getTotalSeats())
                .isActive(bus.getIsActive())
                .createdAt(bus.getCreatedAt())
                .updatedAt(bus.getUpdatedAt())
                .createdBy(bus.getCreatedBy())
                .updatedBy(bus.getUpdatedBy())
                .build();
    }
    
    @Transactional
    public void deleteBusById(Long id) {
        
        // TODO: fetch appointments by bus id
        // TODO: DON'T DELETE, if linked with any appointments
        
        busRepository.deleteById(id);
    }
}
