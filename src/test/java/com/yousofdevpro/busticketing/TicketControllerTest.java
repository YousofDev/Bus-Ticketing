package com.yousofdevpro.busticketing;

import com.yousofdevpro.busticketing.auth.dto.request.RegisterRequestDto;
import com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse;
import com.yousofdevpro.busticketing.reservation.dto.request.AppointmentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.request.BusRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.request.RouteRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.request.TicketRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.response.AppointmentResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.BusResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.RouteResponseDto;
import com.yousofdevpro.busticketing.reservation.dto.response.TicketDetailsResponseDto;
import com.yousofdevpro.busticketing.reservation.repository.AppointmentRepository;
import com.yousofdevpro.busticketing.reservation.repository.TicketRepository;
import com.yousofdevpro.busticketing.reservation.service.AppointmentService;
import com.yousofdevpro.busticketing.reservation.service.BusService;
import com.yousofdevpro.busticketing.reservation.service.RouteService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TicketControllerTest extends BaseIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private BusService busService;
    
    @Autowired
    private RouteService routeService;
    
    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private TicketRepository ticketRepository;
    
    private AppointmentResponseDto appointment;
    private Long customerId;
    
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
    
    private AppointmentResponseDto createAppointment() {
        // Create driver, bus, route
        var bus = createBus();
        var route = createRoute();
        var driver = createDriver();
        
        AppointmentRequestDto appointment = AppointmentRequestDto.builder()
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
        
        var conflictAppointments = appointmentService.getConflictAppointments(appointment);
        if (!conflictAppointments.isEmpty()) {
            return appointmentService.mapToAppointmentDto(conflictAppointments.get(0));
        }
        
        return appointmentService.createAppointment(appointment);
    }
    
    private TicketRequestDto createValidTicketRequest() {
        return TicketRequestDto.builder()
                .status("UNPAID")
                .seatNumber(10)
                .departureDate(LocalDate.now().plusDays(1))
                .appointmentId(appointment.getId())
                .customerUserId(customerId)
                .build();
    }
    
    @BeforeEach
    void setUp() {
        appointment = createAppointment();
        customerId = userService.getUserByEmail("customer@test.com").getId();
    }
    
    @AfterEach
    void clear(){
        ticketRepository.deleteAll();
        appointmentRepository.deleteAll();
    }
    
    @Test
    void shouldCreateTicketSuccessfully() {
        // Given
        TicketRequestDto ticketRequest = createValidTicketRequest();
        
        // When
        ResponseEntity<TicketDetailsResponseDto> response = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ticketRequest.getSeatNumber(), response.getBody().getSeatNumber());
        assertEquals(ticketRequest.getDepartureDate(), response.getBody().getDepartureDate());
        assertEquals("UNPAID", response.getBody().getTicketStatus().name());
    }
    
    @Test
    void shouldNotCreateTicketWithInvalidSeatNumber() {
        // Given
        TicketRequestDto ticketRequest = createValidTicketRequest();
        ticketRequest.setSeatNumber(100); // Invalid seat number
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                String.class);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid seat number"));
    }
    
    @Test
    void shouldNotCreateTicketWithTakenSeat() {
        // Given - Create first ticket
        TicketRequestDto firstTicket = createValidTicketRequest();
        restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(firstTicket, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // When - Try to create second ticket with same seat
        TicketRequestDto secondTicket = createValidTicketRequest();
        secondTicket.setSeatNumber(firstTicket.getSeatNumber()); // Same seat
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(secondTicket, customerHeaders),
                String.class);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("is already taken"));
    }
    
    @Test
    void shouldNotCreateTicketWithPastDepartureDate() {
        // Given
        TicketRequestDto ticketRequest = createValidTicketRequest();
        ticketRequest.setDepartureDate(LocalDate.now().minusDays(1)); // Past date
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                String.class);
        
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("must be today or in the future"));
    }
    
    @Test
    void shouldGetTicketByIdSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        ResponseEntity<TicketDetailsResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        Long ticketId = createResponse.getBody().getId();
        
        // When
        ResponseEntity<TicketDetailsResponseDto> response = restTemplate.exchange(
                "/api/v1/tickets/" + ticketId,
                HttpMethod.GET,
                new HttpEntity<>(customerHeaders),
                TicketDetailsResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ticketId, response.getBody().getId());
    }
    
    @Test
    void shouldNotGetTicketWithInvalidId() {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/tickets/999999",
                HttpMethod.GET,
                new HttpEntity<>(customerHeaders),
                String.class);
        
        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("Ticket not found"));
    }
    
    @Test
    void shouldGetAllTicketsSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // When
        ResponseEntity<List<TicketDetailsResponseDto>> response = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                new ParameterizedTypeReference<>() {
                });
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void shouldUpdateTicketSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        ResponseEntity<TicketDetailsResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        Long ticketId = createResponse.getBody().getId();
        
        // Prepare update
        TicketRequestDto updateRequest = createValidTicketRequest();
        updateRequest.setStatus("PAID");
        updateRequest.setSeatNumber(5); // Change seat number
        
        // When
        ResponseEntity<TicketDetailsResponseDto> response = restTemplate.exchange(
                "/api/v1/tickets/" + ticketId,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest, adminHeaders),
                TicketDetailsResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ticketId, response.getBody().getId());
        assertEquals(5, response.getBody().getSeatNumber());
        assertEquals("PAID", response.getBody().getTicketStatus().name());
    }
    
    @Test
    void shouldPayTicketSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        ResponseEntity<TicketDetailsResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        Long ticketId = createResponse.getBody().getId();
        
        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/tickets/" + ticketId + "/pay",
                HttpMethod.PATCH,
                new HttpEntity<>(customerHeaders),
                Void.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify ticket is now paid
        ResponseEntity<TicketDetailsResponseDto> getResponse = restTemplate.exchange(
                "/api/v1/tickets/" + ticketId,
                HttpMethod.GET,
                new HttpEntity<>(customerHeaders),
                TicketDetailsResponseDto.class);
        assertEquals("PAID", getResponse.getBody().getTicketStatus().name());
    }
    
    @Test
    void shouldCancelTicketSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        ResponseEntity<TicketDetailsResponseDto> createResponse = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        Long ticketId = createResponse.getBody().getId();
        
        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/tickets/" + ticketId + "/cancel",
                HttpMethod.PATCH,
                new HttpEntity<>(adminHeaders),
                Void.class);
        
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        // Verify ticket is now canceled
        ResponseEntity<TicketDetailsResponseDto> getResponse = restTemplate.exchange(
                "/api/v1/tickets/" + ticketId,
                HttpMethod.GET,
                new HttpEntity<>(customerHeaders),
                TicketDetailsResponseDto.class);
        assertEquals("CANCELED", getResponse.getBody().getTicketStatus().name());
    }
    
    @Test
    void shouldGetAllTicketsByCustomerIdSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // When
        ResponseEntity<List<TicketDetailsResponseDto>> response = restTemplate.exchange(
                "/api/v1/tickets/customers/" + customerId,
                HttpMethod.GET,
                new HttpEntity<>(customerHeaders),
                new ParameterizedTypeReference<>() {
                });
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void shouldGetAllTicketsByAppointmentIdSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // When
        ResponseEntity<List<TicketDetailsResponseDto>> response = restTemplate.exchange(
                "/api/v1/tickets/appointments/" + appointment.getId(),
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                new ParameterizedTypeReference<>() {
                });
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void shouldGetAllValidTicketsSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // When
        ResponseEntity<List<TicketDetailsResponseDto>> response = restTemplate.exchange(
                "/api/v1/tickets/valid",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                new ParameterizedTypeReference<>() {
                });
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void shouldGetValidTicketsByCustomerIdSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // When
        ResponseEntity<List<TicketDetailsResponseDto>> response = restTemplate.exchange(
                "/api/v1/tickets/customers/" + customerId + "/valid",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                new ParameterizedTypeReference<>() {
                });
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void shouldGetValidTicketsByAppointmentIdSuccessfully() {
        // Given - Create a ticket first
        TicketRequestDto ticketRequest = createValidTicketRequest();
        restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.POST,
                new HttpEntity<>(ticketRequest, customerHeaders),
                TicketDetailsResponseDto.class);
        
        // When
        ResponseEntity<List<TicketDetailsResponseDto>> response = restTemplate.exchange(
                "/api/v1/tickets/appointments/" + appointment.getId() + "/valid",
                HttpMethod.GET,
                new HttpEntity<>(adminHeaders),
                new ParameterizedTypeReference<>() {
                });
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }
    
    @Test
    void shouldNotAllowCustomerToAccessAdminEndpoints() {
        // When trying to access admin-only endpoint with customer credentials
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/tickets",
                HttpMethod.GET,
                new HttpEntity<>(customerHeaders),
                String.class);
        
        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
    
}
