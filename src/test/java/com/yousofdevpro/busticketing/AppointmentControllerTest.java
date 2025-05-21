package com.yousofdevpro.busticketing;

import com.yousofdevpro.busticketing.auth.dto.request.RegisterRequestDto;
import com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse;
import com.yousofdevpro.busticketing.reservation.dto.request.AppointmentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.request.BusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.request.RouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.response.AppointmentResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.AppointmentSeatsResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.service.BusService;
import com.yousofdevpro.busticketing.reservation.service.RouteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentControllerTest extends BaseIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private BusService busService;
    
    @Autowired
    private RouteService routeService;
    
    private BusResponseDto createBus() {
        // Generate unique bus number for each test
        String uniqueBusNumber = "BUS-" + UUID.randomUUID().toString().substring(0, 8);
        var bus = BusRequestDto.builder()
                .name("Express Bus")
                .busNumber(uniqueBusNumber)
                .totalSeats(40)
                .isActive(true)
                .build();
        
        return busService.addBus(bus);
    }
    
    private RouteResponseDto createRoute() {
        RouteRequestDto route = RouteRequestDto.builder()
                .departurePoint("New York")
                .destinationPoint("Boston")
                .isActive(true)
                .build();
        
        return routeService.addRoute(route);
    }
    
    private UserDtoResponse createDriver() {
        // First check if user already exist
        var user = userService.getUserByEmail("driver@test.com");
        if (user==null) {
            // Create admin user directly via service
            RegisterRequestDto newUser = RegisterRequestDto.builder()
                    .firstName("Driver")
                    .lastName("User")
                    .phone("011221234346")
                    .email("driver@test.com")
                    .password("password123")
                    .passwordAgain("password123")
                    .role("DRIVER")
                    .build();
            
            user = userService.createUser(newUser);
        }
        return user;
    }
    
    
    @Test
    void ShouldReturnActiveAppointments() {
        // Arrange
        HttpEntity<Void> requestEntity = new HttpEntity<>(customerHeaders);
        
        // Act
        ResponseEntity<List<AppointmentSeatsResponseDto>> response = restTemplate.exchange(
                "/api/v1/appointments",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<>() {
                }
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void ShouldCreateAppointmentSuccessfully() {
        // Create driver, bus, route
        var bus = createBus();
        var route = createRoute();
        var driver = createDriver();
        // Arrange
        AppointmentRequestDto requestDto = AppointmentRequestDto.builder()
                .calendarDay("MONDAY")
                .serviceGrade("ECONOMY")
                .price(BigDecimal.valueOf(100))
                .departureTime("08:00")
                .arrivalTime("12:00")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(1))
                .driverUserId(driver.getId())
                .busId(bus.getId())
                .routeId(route.getId())
                .build();
        
        HttpEntity<AppointmentRequestDto> requestEntity =
                new HttpEntity<>(requestDto, adminHeaders);
        
        // Act
        ResponseEntity<AppointmentResponseDto> response = restTemplate.exchange(
                "/api/v1/appointments",
                HttpMethod.POST,
                requestEntity,
                AppointmentResponseDto.class
        );
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("MONDAY", response.getBody().getCalendarDay().name());
        assertEquals(LocalTime.parse("08:00"), response.getBody().getDepartureTime());
    }
    
    @Test
    void ShouldReturnForbiddenForCreatingAppointmentAsCustomer() {
        // Create driver, bus, route
        var bus = createBus();
        var route = createRoute();
        var driver = createDriver();
        
        // Arrange
        AppointmentRequestDto requestDto = AppointmentRequestDto.builder()
                .calendarDay("MONDAY")
                .serviceGrade("ECONOMY")
                .price(BigDecimal.valueOf(100))
                .departureTime("08:00")
                .arrivalTime("12:00")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(1))
                .driverUserId(driver.getId())
                .busId(bus.getId())
                .routeId(route.getId())
                .build();

        HttpEntity<AppointmentRequestDto> requestEntity = new HttpEntity<>(requestDto, customerHeaders);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/appointments",
                HttpMethod.POST,
                requestEntity,
                Void.class
        );

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void ShouldUpdateAppointmentSuccessfullyAsAdmin() {
        // Create driver, bus, route
        var bus = createBus();
        var route = createRoute();
        var driver = createDriver();
        
        // First create an appointment to update
        AppointmentRequestDto createDto = AppointmentRequestDto.builder()
                .calendarDay("TUESDAY")
                .serviceGrade("BUSINESS")
                .price(BigDecimal.valueOf(150))
                .departureTime("09:00")
                .arrivalTime("13:00")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(1))
                .driverUserId(driver.getId())
                .busId(bus.getId())
                .routeId(route.getId())
                .build();

        ResponseEntity<AppointmentResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/appointments",
                HttpMethod.POST,
                new HttpEntity<>(createDto, adminHeaders),
                AppointmentResponseDto.class
        );

        Long appointmentId = createResponse.getBody().getId();

        // Prepare update data
        AppointmentRequestDto updateDto = AppointmentRequestDto.builder()
                .calendarDay("WEDNESDAY")
                .serviceGrade("DELUXE")
                .price(BigDecimal.valueOf(200))
                .departureTime("10:00")
                .arrivalTime("14:00")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(1))
                .driverUserId(driver.getId())
                .busId(bus.getId())
                .routeId(route.getId())
                .build();

        HttpEntity<AppointmentRequestDto> requestEntity = new HttpEntity<>(updateDto, adminHeaders);

        // Act
        ResponseEntity<AppointmentResponseDto> response = restTemplate.exchange(
                "/api/v1/appointments/" + appointmentId,
                HttpMethod.PUT,
                requestEntity,
                AppointmentResponseDto.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("WEDNESDAY", response.getBody().getCalendarDay().name());
        assertEquals(LocalTime.parse("10:00"), response.getBody().getDepartureTime());
    }

    @Test
    void ShouldDeleteAppointmentSuccessfullyAsAdmin() {
        // Create driver, bus, route
        var bus = createBus();
        var route = createRoute();
        var driver = createDriver();
        
        // First create an appointment to delete
        AppointmentRequestDto createDto = AppointmentRequestDto.builder()
                .calendarDay("THURSDAY")
                .serviceGrade("ECONOMY")
                .price(BigDecimal.valueOf(100))
                .departureTime("11:00")
                .arrivalTime("15:00")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(1))
                .driverUserId(driver.getId())
                .busId(bus.getId())
                .routeId(route.getId())
                .build();

        ResponseEntity<AppointmentResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/appointments",
                HttpMethod.POST,
                new HttpEntity<>(createDto, adminHeaders),
                AppointmentResponseDto.class
        );

        Long appointmentId = createResponse.getBody().getId();

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/appointments/" + appointmentId,
                HttpMethod.DELETE,
                new HttpEntity<>(adminHeaders),
                Void.class
        );

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void ShouldReturnBadRequestWhenCreateAppointmentWithInvalidData() {
        // Arrange - missing required fields
        AppointmentRequestDto requestDto = AppointmentRequestDto.builder()
                .calendarDay("INVALID_DAY") // invalid enum value
                .serviceGrade("INVALID_GRADE") // invalid enum value
                .price(BigDecimal.valueOf(-100)) // negative price
                .departureTime("25:00") // invalid time
                .arrivalTime("invalid") // invalid time
                .effectiveDate(LocalDate.now().minusDays(1)) // past date
                .driverUserId(null) // missing required
                .busId(null) // missing required
                .routeId(null) // missing required
                .build();

        HttpEntity<AppointmentRequestDto> requestEntity = new HttpEntity<>(requestDto, adminHeaders);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/appointments",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void ShouldReturnNotFoundForUpdateNonExistentAppointment() {
        // Create driver, bus, route
        var bus = createBus();
        var route = createRoute();
        var driver = createDriver();
        
        // Arrange
        Long nonExistentId = 9999L;
        AppointmentRequestDto updateDto = AppointmentRequestDto.builder()
                .calendarDay("FRIDAY")
                .serviceGrade("ECONOMY")
                .price(BigDecimal.valueOf(100))
                .departureTime("08:00")
                .arrivalTime("12:00")
                .effectiveDate(LocalDate.now().plusDays(1))
                .endDate(LocalDate.now().plusMonths(1))
                .driverUserId(driver.getId())
                .busId(bus.getId())
                .routeId(route.getId())
                .build();

        HttpEntity<AppointmentRequestDto> requestEntity = new HttpEntity<>(updateDto, adminHeaders);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/appointments/" + nonExistentId,
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
