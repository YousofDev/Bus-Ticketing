package com.yousofdevpro.busticketing.auth.controller;

import com.yousofdevpro.busticketing.auth.dto.*;
import com.yousofdevpro.busticketing.auth.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<LoginResponseDto>login(
            @Validated @RequestBody LoginRequestDto loginRequestDto){
        var loginResponse = authService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponse);
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponseDto> resetPassword(
            @Validated @RequestBody EmailRequestDto emailRequestDto) {
        var message = authService.resetPassword(emailRequestDto);
        return ResponseEntity.ok(message);
    }
    
    @PostMapping("/reset-password-confirm")
    public ResponseEntity<MessageResponseDto> resetPasswordVerify(
            @Validated @RequestBody ResetPasswordConfirmRequestDto resetPasswordConfirmDto) {
        var message = authService.resetPasswordConfirm(
                resetPasswordConfirmDto);
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
    
}
