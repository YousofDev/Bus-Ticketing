package com.yousofdevpro.busticketing;

import com.yousofdevpro.busticketing.reservation.dto.request.BusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.response.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BusControllerTest extends BaseIntegrationTest {
    private HttpHeaders headers;
    
    private BusRequestDto createValidBusRequest() {
        // Generate unique bus number for each test
        String uniqueBusNumber = "BUS-" + UUID.randomUUID().toString().substring(0, 8);
        return BusRequestDto.builder()
                .name("Express Bus")
                .busNumber(uniqueBusNumber)
                .totalSeats(40)
                .isActive(true)
                .build();
    }
    
    @BeforeEach
    void setUpAuthentication() {
        var adminToken = getAdminToken();
        // Set up headers with JWT token
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        headers.set("Content-Type", "application/json");
    }
    
    @Test
    void shouldCreateBus() {
        BusRequestDto request = createValidBusRequest();
        
        HttpEntity<BusRequestDto> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<BusResponseDto> response = restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.POST,
                requestEntity,
                BusResponseDto.class
        );
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Express Bus", response.getBody().getName());
        assertEquals(40, response.getBody().getTotalSeats());
        assertTrue(response.getBody().getIsActive());
        assertNotNull(response.getBody().getId());
    }
    
    @Test
    void shouldFailCreateBusWithInvalidData() {
        BusRequestDto invalidRequest = BusRequestDto.builder()
                .name("")  // Blank name
                .busNumber("")  // Blank bus number
                .totalSeats(0)  // Below minimum
                .isActive(null)  // Null boolean
                .build();
        
        HttpEntity<BusRequestDto> requestEntity = new HttpEntity<>(invalidRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("name is required"));
        assertTrue(response.getBody().contains("busNumber is required"));
        assertTrue(response.getBody().contains("totalSeats must be at least 1"));
        assertTrue(response.getBody().contains("isActive is required"));
    }
    
    @Test
    void shouldGetAllBuses() {
        // Create a test bus first
        BusRequestDto request = createValidBusRequest();
        restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                BusResponseDto.class
        );
        
        ResponseEntity<BusResponseDto[]> response = restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BusResponseDto[].class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }
    
    @Test
    void shouldGetBusById() {
        // First create a bus
        BusRequestDto request = createValidBusRequest();
        ResponseEntity<BusResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                BusResponseDto.class
        );
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        
        // Then get it by ID
        Long busId = createResponse.getBody().getId();
        ResponseEntity<BusResponseDto> response = restTemplate.exchange(
                "/api/v1/buses/" + busId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BusResponseDto.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(busId, response.getBody().getId());
        assertEquals(request.getName(), response.getBody().getName());
    }
    
    @Test
    void shouldUpdateBus() {
        // First create a bus
        BusRequestDto createRequest = createValidBusRequest();
        ResponseEntity<BusResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.POST,
                new HttpEntity<>(createRequest, headers),
                BusResponseDto.class
        );
        
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        Long busId = createResponse.getBody().getId();
        
        // Then update it
        String updatedBusNumber = "BUS-" + UUID.randomUUID().toString().substring(0, 8);
        BusRequestDto updateRequest = BusRequestDto.builder()
                .name("Updated Express Bus")
                .busNumber(updatedBusNumber)
                .totalSeats(50)
                .isActive(false)
                .build();
        
        ResponseEntity<BusResponseDto> response = restTemplate.exchange(
                "/api/v1/buses/" + busId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest, headers),
                BusResponseDto.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Express Bus", response.getBody().getName());
        assertEquals(updatedBusNumber, response.getBody().getBusNumber());
        assertEquals(50, response.getBody().getTotalSeats());
        assertFalse(response.getBody().getIsActive());
    }
    
    @Test
    void shouldReturnNotFoundForInvalidBusId() {
        ResponseEntity<BusResponseDto> response = restTemplate.exchange(
                "/api/v1/buses/999999", // Use a very high ID that likely doesn't exist
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BusResponseDto.class
        );
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void shouldFailUpdateWithInvalidBusId() {
        BusRequestDto updateRequest = createValidBusRequest();
        ResponseEntity<BusResponseDto> response = restTemplate.exchange(
                "/api/v1/buses/999999878548484845", // Use a very high ID that likely doesn't exist
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest, headers),
                BusResponseDto.class
        );
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void shouldDeleteBus() {
        // First create a bus
        BusRequestDto createRequest = createValidBusRequest();
        ResponseEntity<BusResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.POST,
                new HttpEntity<>(createRequest, headers),
                BusResponseDto.class
        );
        
        // Then delete it
        Long busId = createResponse.getBody().getId();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/buses/" + busId,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        
        // Verify it's deleted
        ResponseEntity<BusResponseDto> getResponse = restTemplate.exchange(
                "/api/v1/buses/" + busId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                BusResponseDto.class
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
    
    @Test
    void shouldForbidNonAdminToCreateBus() {
        // Create a regular user token (customer role)
        String customerToken = getCustomerToken("customer@test.com");
        
        HttpHeaders customerHeaders = new HttpHeaders();
        customerHeaders.set("Authorization", "Bearer " + customerToken);
        customerHeaders.set("Content-Type", "application/json");
        
        BusRequestDto request = createValidBusRequest();
        HttpEntity<BusRequestDto> requestEntity = new HttpEntity<>(request, customerHeaders);
        
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/buses",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
}
