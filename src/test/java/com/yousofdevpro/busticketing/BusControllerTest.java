package com.yousofdevpro.busticketing;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yousofdevpro.busticketing.reservation.dto.request.BusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.response.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.service.BusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class BusControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BusService busService;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }
    
    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF", "DRIVER", "CUSTOMER"})
    void testGetBuses() throws Exception {
        List<BusResponseDto> buses = Arrays.asList(
                new BusResponseDto(1L, "Bus 1", "1234", 40, true, null, null, null, null),
                new BusResponseDto(2L, "Bus 2", "5678", 30, true, null, null, null, null)
        );
        
        when(busService.getBuses()).thenReturn(buses);
        
        mockMvc.perform(get("/api/v1/buses"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Bus 1"))
                .andExpect(jsonPath("$[1].name").value("Bus 2"))
                .andDo(print());
    }
    
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddBus() throws Exception {
        BusRequestDto busRequestDto = new BusRequestDto();
        busRequestDto.setName("New Bus");
        busRequestDto.setBusNumber("9999");
        busRequestDto.setTotalSeats(50);
        busRequestDto.setIsActive(true);
        
        BusResponseDto createdBus = new BusResponseDto(3L, "New Bus", "9999", 50, true, null, null, null, null);
        
        when(busService.addBus(any(BusRequestDto.class))).thenReturn(createdBus);
        
        mockMvc.perform(post("/api/v1/buses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(busRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Bus"))
                .andDo(print());
    }
    
    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF", "DRIVER", "CUSTOMER"})
    void testGetBus() throws Exception {
        Long busId = 1L;
        BusResponseDto busResponse = new BusResponseDto(busId, "Bus 1", "1234", 40, true, null, null, null, null);
        
        when(busService.getBusById(busId)).thenReturn(busResponse);
        
        mockMvc.perform(get("/api/v1/buses/{id}", busId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bus 1"))
                .andDo(print());
    }
    
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateBus() throws Exception {
        Long busId = 1L;
        BusRequestDto updateRequest = new BusRequestDto();
        updateRequest.setName("Updated Bus");
        updateRequest.setBusNumber("1234");
        updateRequest.setTotalSeats(40);
        updateRequest.setIsActive(true);
        
        BusResponseDto updatedBus = new BusResponseDto(busId, "Updated Bus", "1234", 40, true, null, null, null, null);
        
        when(busService.updateBusById(any(BusRequestDto.class), eq(busId))).thenReturn(updatedBus);
        
        mockMvc.perform(put("/api/v1/buses/{id}", busId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Bus"))
                .andDo(print());
    }
    
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteBus() throws Exception {
        Long busId = 1L;
        
        doNothing().when(busService).deleteBusById(busId);
        
        mockMvc.perform(delete("/api/v1/buses/{id}", busId))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
