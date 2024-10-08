package com.yousofdevpro.busticketing.auth.service;

import com.yousofdevpro.busticketing.auth.dto.LoginRequestDto;
import com.yousofdevpro.busticketing.auth.dto.LoginResponseDto;
import com.yousofdevpro.busticketing.auth.dto.RegisterRequestDto;
import com.yousofdevpro.busticketing.auth.dto.RegisterResponseDto;
import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.config.exception.ConflictException;
import com.yousofdevpro.busticketing.config.exception.ResourceNotFoundException;
import com.yousofdevpro.busticketing.config.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {
        
        Optional<User> existedUser = userRepository.findByEmail(registerRequestDto.getEmail());
        
        if (existedUser.isPresent()) {
            throw new ConflictException("User already registered");
        }
        
        var user = User.builder()
                .firstName(registerRequestDto.getFirstName())
                .lastName(registerRequestDto.getLastName())
                .phone(registerRequestDto.getPhone())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(Role.valueOf(registerRequestDto.getRole()))
                .build();
        
        user = userRepository.save(user);
        
        return RegisterResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .build();
        
    }
    
    
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
        
        String accessToken = jwtUtil.generateToken(loginRequestDto.getEmail());
        
        return new LoginResponseDto(accessToken);
    }
}
