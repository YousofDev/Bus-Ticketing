package com.yousofdevpro.busticketing.auth.controller;

import com.yousofdevpro.busticketing.auth.dto.request.*;
import com.yousofdevpro.busticketing.auth.dto.response.MessageResponseDto;
import com.yousofdevpro.busticketing.auth.dto.response.TokensResponseDto;
import com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse;
import com.yousofdevpro.busticketing.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<MessageResponseDto> register(
            @Validated @RequestBody RegisterRequestDto registerRequestDto){
        var message = authService.register(registerRequestDto);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }
    
    @PostMapping("/confirm-account")
    public ResponseEntity<MessageResponseDto> confirmAccount(
            @Validated @RequestBody ConfirmationCodeRequestDto confirmationCodeRequestDto) {
        var message = authService.confirmAccount(confirmationCodeRequestDto);
        return ResponseEntity.ok(message);
    }
    
    @PostMapping("/login")
    public ResponseEntity<TokensResponseDto>login(
            @Validated @RequestBody LoginRequestDto loginRequestDto){
        var tokens = authService.login(loginRequestDto);
        return ResponseEntity.ok(tokens);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDto> resetPassword(
            @Validated @RequestBody EmailRequestDto emailRequestDto) {
        var message = authService.resetPassword(emailRequestDto);
        return ResponseEntity.ok(message);
    }
    
    @PostMapping("/reset-password-confirm")
    public ResponseEntity<MessageResponseDto> resetPasswordVerify(
            @Validated @RequestBody ResetPasswordConfirmRequestDto resetPasswordConfirmRequestDto) {
        var message = authService.resetPasswordConfirm(
                resetPasswordConfirmRequestDto);
        return ResponseEntity.ok(message);
    }
    
    @PostMapping("/send-confirmation-code")
    public ResponseEntity<MessageResponseDto> sendConfirmationCode(
            @Validated @RequestBody EmailRequestDto emailRequestDto) {
        var message = authService.sendConfirmationCode(emailRequestDto);
        return ResponseEntity.ok(message);
    }
    
    @PostMapping("/verify-confirmation-code")
    public ResponseEntity<MessageResponseDto> verifyConfirmationCode(
            @Validated @RequestBody ConfirmationCodeRequestDto confirmationCodeRequestDto) {
        var message = authService.verifyConfirmationCode(
                confirmationCodeRequestDto);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/profile")
    public UserDtoResponse getUserProfile(
            @AuthenticationPrincipal UserDetails userDetails){
        return authService.getUserProfile(userDetails);
    }
    
    @PutMapping("/profile")
    public UserDtoResponse updateUserProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Validated @RequestBody ProfileRequestDto profileRequestDto) {
        return authService.updateUserProfile(userDetails, profileRequestDto);
    }
    
    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Validated @RequestBody ChangePasswordRequestDto changePasswordRequestDto) {
        authService.changePassword(userDetails, changePasswordRequestDto);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<TokensResponseDto> refreshToken(
            @AuthenticationPrincipal UserDetails userDetails) {
        var tokens = authService.refreshToken(userDetails);
        return ResponseEntity.ok(tokens);
    }
    
}
