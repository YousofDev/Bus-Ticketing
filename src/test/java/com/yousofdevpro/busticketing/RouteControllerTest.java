package com.yousofdevpro.busticketing;

import com.yousofdevpro.busticketing.reservation.dto.request.RouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.response.RouteResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class RouteControllerTest extends BaseIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    private HttpHeaders headers;
    private HttpHeaders customerHeaders;
    
    @BeforeEach
    void setUpAuthentication() {
        var adminToken = getAdminToken();
        var customerToken = getCustomerToken("customer@test.com");
        
        // Set up headers for admin
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        headers.set("Content-Type", "application/json");
        
        // Set up headers for customer
        customerHeaders = new HttpHeaders();
        customerHeaders.set("Authorization", "Bearer " + customerToken);
        customerHeaders.set("Content-Type", "application/json");
    }
    
    @Test
    void shouldCreateRouteSuccessfully() {
        // Given
        RouteRequestDto request = RouteRequestDto.builder()
                .departurePoint("New York")
                .destinationPoint("Boston")
                .isActive(true)
                .build();
        
        HttpEntity<RouteRequestDto> requestEntity = new HttpEntity<>(request, headers);
        
        // When
        ResponseEntity<RouteResponseDto> response = restTemplate.exchange(
                "/api/v1/routes",
                HttpMethod.POST,
                requestEntity,
                RouteResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("New York", response.getBody().getDeparturePoint());
        assertEquals("Boston", response.getBody().getDestinationPoint());
        assertTrue(response.getBody().getIsActive());
    }
    
    @Test
    void shouldReturnBadRequestWhenCreatingRouteWithInvalidData() {
        // Given
        RouteRequestDto request = RouteRequestDto.builder()
                .departurePoint("")  // Invalid - blank
                .destinationPoint("") // Invalid - blank
                .isActive(null)       // Invalid - null
                .build();
        
        HttpEntity<RouteRequestDto> requestEntity = new HttpEntity<>(request, headers);
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/routes",
                HttpMethod.POST,
                requestEntity,
                String.class);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("departurePoint is required"));
        assertTrue(response.getBody().contains("destinationPoint is required"));
        assertTrue(response.getBody().contains("isActive is required"));
    }
    
    @Test
    void shouldReturnForbiddenWhenCreatingRouteWithoutAdminRole() {
        // Given
        RouteRequestDto request = RouteRequestDto.builder()
                .departurePoint("New York")
                .destinationPoint("Boston")
                .isActive(true)
                .build();
        
        HttpEntity<RouteRequestDto> requestEntity = new HttpEntity<>(request, customerHeaders);
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/routes",
                HttpMethod.POST,
                requestEntity,
                String.class);
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    void shouldGetAllRoutesSuccessfully() {
        // Given - First create a route to ensure there's data
        createTestRoute("Chicago", "Detroit", true);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        
        // When
        ResponseEntity<List> response = restTemplate.exchange(
                "/api/v1/routes",
                HttpMethod.GET,
                requestEntity,
                List.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void shouldGetRouteByIdSuccessfully() {
        // Given - First create a route
        RouteResponseDto createdRoute = createTestRoute("Los Angeles", "San Francisco", true);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        
        // When
        ResponseEntity<RouteResponseDto> response = restTemplate.exchange(
                "/api/v1/routes/" + createdRoute.getId(),
                HttpMethod.GET,
                requestEntity,
                RouteResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdRoute.getId(), response.getBody().getId());
        assertEquals("Los Angeles", response.getBody().getDeparturePoint());
        assertEquals("San Francisco", response.getBody().getDestinationPoint());
    }
    
    @Test
    void shouldReturnNotFoundWhenGettingNonExistentRoute() {
        // Given
        long nonExistentId = 9999L;
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/routes/" + nonExistentId,
                HttpMethod.GET,
                requestEntity,
                String.class);
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).contains("Route not found"));
    }
    
    @Test
    void shouldUpdateRouteSuccessfully() {
        // Given - First create a route
        RouteResponseDto createdRoute = createTestRoute("Seattle", "Portland", true);
        
        RouteRequestDto updateRequest = RouteRequestDto.builder()
                .departurePoint("Seattle Updated")
                .destinationPoint("Portland Updated")
                .isActive(false)
                .build();
        
        HttpEntity<RouteRequestDto> requestEntity = new HttpEntity<>(updateRequest, headers);
        
        // When
        ResponseEntity<RouteResponseDto> response = restTemplate.exchange(
                "/api/v1/routes/" + createdRoute.getId(),
                HttpMethod.PUT,
                requestEntity,
                RouteResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdRoute.getId(), response.getBody().getId());
        assertEquals("Seattle Updated", response.getBody().getDeparturePoint());
        assertEquals("Portland Updated", response.getBody().getDestinationPoint());
        assertFalse(response.getBody().getIsActive());
    }
    
    @Test
    void shouldReturnForbiddenWhenUpdatingRouteWithoutAdminRole() {
        // Given - First create a route
        RouteResponseDto createdRoute = createTestRoute("Dallas", "Houston", true);
        
        RouteRequestDto updateRequest = RouteRequestDto.builder()
                .departurePoint("Dallas Updated")
                .destinationPoint("Houston Updated")
                .isActive(false)
                .build();
        
        HttpEntity<RouteRequestDto> requestEntity = new HttpEntity<>(updateRequest, customerHeaders);
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/routes/" + createdRoute.getId(),
                HttpMethod.PUT,
                requestEntity,
                String.class);
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    void shouldDeleteRouteSuccessfully() {
        // Given - First create a route
        RouteResponseDto createdRoute = createTestRoute("Miami", "Orlando", true);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        
        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/routes/" + createdRoute.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify the route is actually deleted
        ResponseEntity<String> getResponse = restTemplate.exchange(
                "/api/v1/routes/" + createdRoute.getId(),
                HttpMethod.GET,
                requestEntity,
                String.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }
    
    @Test
    void shouldReturnForbiddenWhenDeletingRouteWithoutAdminRole() {
        // Given - First create a route
        RouteResponseDto createdRoute = createTestRoute("Denver", "Boulder", true);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(null, customerHeaders);
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/routes/" + createdRoute.getId(),
                HttpMethod.DELETE,
                requestEntity,
                String.class);
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
    @Test
    void shouldGetRouteWithCustomerRole() {
        // Given - First create a route
        RouteResponseDto createdRoute = createTestRoute("Phoenix", "Tucson", true);
        
        HttpEntity<String> requestEntity = new HttpEntity<>(null, customerHeaders);
        
        // When
        ResponseEntity<RouteResponseDto> response = restTemplate.exchange(
                "/api/v1/routes/" + createdRoute.getId(),
                HttpMethod.GET,
                requestEntity,
                RouteResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdRoute.getId(), response.getBody().getId());
    }
    
    // Helper method to create a test route
    private RouteResponseDto createTestRoute(String departure, String destination, boolean isActive) {
        RouteRequestDto request = RouteRequestDto.builder()
                .departurePoint(departure)
                .destinationPoint(destination)
                .isActive(isActive)
                .build();
        
        HttpEntity<RouteRequestDto> requestEntity = new HttpEntity<>(request, headers);
        ResponseEntity<RouteResponseDto> response = restTemplate.exchange(
                "/api/v1/routes",
                HttpMethod.POST,
                requestEntity,
                RouteResponseDto.class);
        
        return response.getBody();
    }
}
