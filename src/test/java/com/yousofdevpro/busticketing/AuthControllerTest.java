package com.yousofdevpro.busticketing;

import com.yousofdevpro.busticketing.auth.dto.request.*;
import com.yousofdevpro.busticketing.auth.dto.response.MessageResponseDto;
import com.yousofdevpro.busticketing.auth.dto.response.TokensResponseDto;
import com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse;
import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.auth.service.UserService;
import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AuthControllerTest extends BaseIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private String userEmail;
    private Long userId;
    private HttpHeaders userHeaders;
    
    private User createUser(){
        // Create test user if not exists
        return userRepository.findByEmail("confirmed@test.com").orElseGet(() -> {
            User newUser = User.builder()
                    .firstName("Test")
                    .lastName("User")
                    .phone("01123456879")
                    .email("confirmed@test.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(Role.CUSTOMER)
                    .isConfirmed(true)
                    .isActive(true)
                    .build();
            return userRepository.save(newUser);
        });
    }
    
    @BeforeEach
    void setup(){
        User user = createUser();
        userEmail = user.getEmail();
        userId = user.getId();
        userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(getCustomerToken(userEmail));
    }
    
    @AfterEach
    void clear(){
        userRepository.deleteById(userId);
    }
    
    @Test
    void shouldRegisterUserSuccessfully() {
        // Given
        RegisterRequestDto registerRequest = RegisterRequestDto.builder()
                .firstName("New")
                .lastName("User")
                .phone("01199844132")
                .email("newUser@test.com")
                .password("password123")
                .passwordAgain("password123")
                .role("CUSTOMER")
                .build();
        
        // When
        ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/register",
                HttpMethod.POST,
                new HttpEntity<>(registerRequest),
                MessageResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("We have sent a confirmation code"));
        
        // Verify user was created
        User createdUser = userRepository.findByEmail(registerRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));
        assertEquals("New", createdUser.getFirstName());
        assertEquals("User", createdUser.getLastName());
    }
    
    @Test
    void shouldConfirmAccountSuccessfully() {
        // First create an unconfirmed user
        User unconfirmedUser = User.builder()
                .firstName("Unconfirmed")
                .lastName("User")
                .phone("01104878565")
                .email("unconfirmed@test.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.CUSTOMER)
                .isConfirmed(false)
                .isActive(true)
                .otpCode("123456")
                .otpCodeExpiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        userRepository.save(unconfirmedUser);
        
        // Given
        ConfirmationCodeRequestDto confirmRequest = new ConfirmationCodeRequestDto();
        confirmRequest.setEmail("unconfirmed@test.com");
        confirmRequest.setCode("123456");
        
        // When
        ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/confirm-account",
                HttpMethod.POST,
                new HttpEntity<>(confirmRequest),
                MessageResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("confirmed"));
        
        // Verify user is now confirmed
        User confirmedUser = userRepository.findByEmail("unconfirmed@test.com")
                .orElseThrow(() -> new NotFoundException("User not found"));
        assertTrue(confirmedUser.getIsConfirmed());
    }
    
    @Test
    void shouldLoginUserSuccessfully() {
        
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto(userEmail, "password123");
        
        // When
        ResponseEntity<TokensResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                TokensResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
    }
    
    @Test
    void shouldNotLoginWithInvalidCredentials() {
        // Given
        LoginRequestDto loginRequest = new LoginRequestDto(userEmail, "wrongpassword");
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                String.class);
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid"));
    }
    
    @Test
    void shouldGetUserProfileSuccessfully() {
        // When
        ResponseEntity<UserDtoResponse> response = restTemplate.exchange(
                "/api/v1/auth/profile",
                HttpMethod.GET,
                new HttpEntity<>(userHeaders),
                UserDtoResponse.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userEmail, response.getBody().getEmail());
    }
    
    @Test //
    void shouldUpdateUserProfileSuccessfully() {
        // Given
        ProfileRequestDto profileRequest = ProfileRequestDto.builder()
                .firstName("Updated")
                .lastName("Name")
                .phone("01199999999")
                .email(userEmail)
                .role("CUSTOMER")
                .build();
        
        // When
        ResponseEntity<UserDtoResponse> response = restTemplate.exchange(
                "/api/v1/auth/profile",
                HttpMethod.PUT,
                new HttpEntity<>(profileRequest, userHeaders),
                UserDtoResponse.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated", response.getBody().getFirstName());
        assertEquals("Name", response.getBody().getLastName());
        
        // Verify update in database
        User updatedUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        assertEquals("Updated", updatedUser.getFirstName());
    }
    
    @Test //
    void shouldChangePasswordSuccessfully() {
        // Given
        ChangePasswordRequestDto changePasswordRequest = new ChangePasswordRequestDto();
        changePasswordRequest.setCurrentPassword("password123");
        changePasswordRequest.setPassword("newpassword123");
        changePasswordRequest.setPasswordAgain("newpassword123");
        
        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/v1/auth/change-password",
                HttpMethod.PATCH,
                new HttpEntity<>(changePasswordRequest, userHeaders),
                Void.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Verify password was changed by trying to log in with new password
        LoginRequestDto loginRequest = new LoginRequestDto(userEmail, "newpassword123");
        ResponseEntity<TokensResponseDto> loginResponse = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                TokensResponseDto.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    }
    
    @Test
    void shouldNotChangePasswordWithWrongCurrentPassword() {
        // Given
        ChangePasswordRequestDto changePasswordRequest = new ChangePasswordRequestDto();
        changePasswordRequest.setCurrentPassword("wrongpassword");
        changePasswordRequest.setPassword("newpassword123");
        changePasswordRequest.setPasswordAgain("newpassword123");
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/auth/change-password",
                HttpMethod.PATCH,
                new HttpEntity<>(changePasswordRequest, userHeaders),
                String.class);
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("incorrect"));
    }
    
    @Test
    void shouldSendConfirmationCodeSuccessfully() {
        // Given
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setEmail(userEmail);
        
        // When
        ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/send-confirmation-code",
                HttpMethod.POST,
                new HttpEntity<>(emailRequest),
                MessageResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("sent"));
        
        // Verify OTP code was set
        User userWithOtp = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        assertNotNull(userWithOtp.getOtpCode());
        assertNotNull(userWithOtp.getOtpCodeExpiresAt());
    }
    
    @Test
    void shouldVerifyConfirmationCodeSuccessfully() {
        // First send confirmation code
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setEmail(userEmail);
        restTemplate.exchange(
                "/api/v1/auth/send-confirmation-code",
                HttpMethod.POST,
                new HttpEntity<>(emailRequest),
                MessageResponseDto.class);
        
        // Get the OTP code from database
        User userWithOtp = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String otpCode = userWithOtp.getOtpCode();
        
        // Given
        ConfirmationCodeRequestDto codeRequest = new ConfirmationCodeRequestDto();
        codeRequest.setEmail(userEmail);
        codeRequest.setCode(otpCode);
        
        // When
        ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/verify-confirmation-code",
                HttpMethod.POST,
                new HttpEntity<>(codeRequest),
                MessageResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("verified"));
    }
    
    @Test
    void shouldNotVerifyInvalidConfirmationCode() {
        // Given
        ConfirmationCodeRequestDto codeRequest = new ConfirmationCodeRequestDto();
        codeRequest.setEmail(userEmail);
        codeRequest.setCode("INVALIDCODE");
        
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/v1/auth/verify-confirmation-code",
                HttpMethod.POST,
                new HttpEntity<>(codeRequest),
                String.class);
        
        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid"));
    }
    
    @Test //
    void shouldRefreshTokenSuccessfully() {
        // First login to get refresh token
        LoginRequestDto loginRequest = new LoginRequestDto(userEmail, "password123");
        ResponseEntity<TokensResponseDto> loginResponse = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                TokensResponseDto.class);
        String refreshToken = loginResponse.getBody().getRefreshToken();
        
        // Set refresh token in headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(refreshToken);
        
        // When
        ResponseEntity<TokensResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/refresh-token",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                TokensResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());
    }
    
    @Test
    void shouldResetPasswordSuccessfully() {
        // Given
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setEmail(userEmail);
        
        // When
        ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/reset-password",
                HttpMethod.POST,
                new HttpEntity<>(emailRequest),
                MessageResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("sent"));
        
        // Verify OTP code was set
        User userWithOtp = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        assertNotNull(userWithOtp.getOtpCode());
        assertNotNull(userWithOtp.getOtpCodeExpiresAt());
    }
    
    @Test
    void shouldConfirmResetPasswordSuccessfully() {
        // First request password reset to get OTP
        EmailRequestDto emailRequest = new EmailRequestDto();
        emailRequest.setEmail(userEmail);
        restTemplate.exchange(
                "/api/v1/auth/reset-password",
                HttpMethod.POST,
                new HttpEntity<>(emailRequest),
                MessageResponseDto.class);
        
        // Get the OTP code from database
        User userWithOtp = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String otpCode = userWithOtp.getOtpCode();
        
        // Given
        ResetPasswordConfirmRequestDto resetRequest = new ResetPasswordConfirmRequestDto();
        resetRequest.setEmail(userEmail);
        resetRequest.setCode(otpCode);
        resetRequest.setPassword("newpassword123");
        resetRequest.setPasswordAgain("newpassword123");
        
        // When
        ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                "/api/v1/auth/reset-password-confirm",
                HttpMethod.POST,
                new HttpEntity<>(resetRequest),
                MessageResponseDto.class);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("successfully"));
        
        // Verify password was changed by trying to log in with new password
        LoginRequestDto loginRequest = new LoginRequestDto(userEmail, "newpassword123");
        ResponseEntity<TokensResponseDto> loginResponse = restTemplate.exchange(
                "/api/v1/auth/login",
                HttpMethod.POST,
                new HttpEntity<>(loginRequest),
                TokensResponseDto.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
    }
    
    
    
    
}
