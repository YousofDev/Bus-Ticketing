package com.yousofdevpro.busticketing.auth.controller;

import com.yousofdevpro.busticketing.auth.dto.LoginRequestDto;
import com.yousofdevpro.busticketing.auth.dto.LoginResponseDto;
import com.yousofdevpro.busticketing.auth.dto.RegisterRequestDto;
import com.yousofdevpro.busticketing.auth.dto.RegisterResponseDto;
import com.yousofdevpro.busticketing.auth.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDto> register(
            @Validated @RequestBody RegisterRequestDto registerRequestDto){
        var createdUser = authService.register(registerRequestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto>login(
            @Validated @RequestBody LoginRequestDto loginRequestDto){
        var loginResponse = authService.login(loginRequestDto);
        return ResponseEntity.ok(loginResponse);
    }
    
}
