package com.yousofdevpro.busticketing;

import com.yousofdevpro.busticketing.auth.dto.request.LoginRequestDto;
import com.yousofdevpro.busticketing.auth.dto.request.RegisterRequestDto;
import com.yousofdevpro.busticketing.auth.dto.response.TokensResponseDto;
import com.yousofdevpro.busticketing.auth.service.AuthService;
import com.yousofdevpro.busticketing.auth.service.UserService;
import com.yousofdevpro.busticketing.core.security.JwtUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;

import java.util.logging.Logger;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    
    @Autowired
    protected TestRestTemplate restTemplate;
    
    @Autowired
    protected UserService userService;
    
    protected HttpHeaders adminHeaders;
    protected HttpHeaders customerHeaders;
    
    @BeforeEach
    void setUpAuthentication() {
        adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(getAdminToken());
        
        customerHeaders = new HttpHeaders();
        customerHeaders.setBearerAuth(getCustomerToken("customer@test.com"));
    }
    
    protected String getAdminToken() {
        // First check if user already exist
        var adminUser = userService.getUserByEmail("admin@test.com");
        if (adminUser==null) {
            // Create admin user directly via service
            RegisterRequestDto newAdminUser = RegisterRequestDto.builder()
                    .firstName("Admin")
                    .lastName("User")
                    .phone("01122123456")
                    .email("admin@test.com")
                    .password("password123")
                    .passwordAgain("password123")
                    .role("ADMIN")
                    .build();
            
            userService.createUser(newAdminUser);
        }
        
        
        // Login to get JWT token
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                "admin@test.com", "password123");
        
        TokensResponseDto tokensResponseDto = restTemplate.postForObject(
                "/api/v1/auth/login",
                loginRequestDto,
                TokensResponseDto.class);
        
        return tokensResponseDto.getAccessToken();
    }
    
    protected String getCustomerToken(String email){
        // First check if user already exist
        var customerUser = userService.getUserByEmail(email);
        if (customerUser==null) {
            // Create admin user directly via service
            RegisterRequestDto newCustomerUser = RegisterRequestDto.builder()
                    .firstName("Customer")
                    .lastName("User")
                    .phone("01157889451")
                    .email(email)
                    .password("password123")
                    .passwordAgain("password123")
                    .role("CUSTOMER")
                    .build();
            
            userService.createUser(newCustomerUser);
        }
        
        // Login to get JWT token
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                email, "password123");
        
        TokensResponseDto tokensResponseDto = restTemplate.postForObject(
                "/api/v1/auth/login",
                loginRequestDto,
                TokensResponseDto.class);
        
        return tokensResponseDto.getAccessToken();
    }
}
