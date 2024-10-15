package com.yousofdevpro.busticketing.auth.service;

import com.yousofdevpro.busticketing.auth.dto.*;
import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.core.exception.AuthenticationException;
import com.yousofdevpro.busticketing.core.exception.AuthorizationException;
import com.yousofdevpro.busticketing.core.exception.BadRequestException;
import com.yousofdevpro.busticketing.core.notification.EmailService;
import com.yousofdevpro.busticketing.core.security.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public MessageResponseDto register(RegisterRequestDto registerRequestDto) {
        
        if (!registerRequestDto.getPassword().equals(
                registerRequestDto.getPasswordAgain())) {
            throw new BadRequestException("The two password fields should be the same!");
        }
        
        String successMessage = "We've sent a confirmation code to " +
                registerRequestDto.getEmail();
        
        String privateMessage = "It appears there was an attempt to register a new account " +
                "using your email. If this was you, please note that your email " +
                "is already registered in our system.";
        
        Optional<User> existUser = userRepository.findByEmail(registerRequestDto.getEmail());
        
        if (existUser.isPresent()) {
            sendAlertMessage(existUser.get(), privateMessage);
            return new MessageResponseDto(successMessage);
        }
        
        var user = User.builder()
                .firstName(registerRequestDto.getFirstName())
                .lastName(registerRequestDto.getLastName())
                .phone(registerRequestDto.getPhone())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(Role.valueOf(registerRequestDto.getRole()))
                .isConfirmed(false)
                .otpCode(generateOtpCode())
                .otpCodeExpiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        
        user = userRepository.save(user);
        
        user.setCreatedBy(user.getId());
        user = userRepository.save(user);
        
        sendConfirmationMessage(user, "Please, use this code to confirm your account");
        
        return new MessageResponseDto(successMessage);
    }
    
    public MessageResponseDto confirmAccount(ConfirmationCodeRequestDto confirmAccountDto) {
        
        Optional<User> existUser = userRepository.findByEmail(confirmAccountDto.getEmail());
        
        User user = null;
        boolean isOtpNotValid = true;
        if (existUser.isPresent()) {
            user = existUser.get();
            isOtpNotValid = user.isOtpNotValid(confirmAccountDto.getCode());
        }
        
        if (user==null || isOtpNotValid) {
            throw new AuthenticationException("Invalid confirmation code or email");
        }
        
        user.setIsConfirmed(true);
        user.setOtpCode(null);
        user.setOtpCodeExpiresAt(null);
        user = userRepository.save(user);
        
        return new MessageResponseDto("Your account has been confirmed successfully");
    }
    
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        
        Optional<User> existUser = userRepository.findByEmail(loginRequestDto.getEmail());
        
        User user = null;
        if (existUser.isPresent()) {
            user = existUser.get();
        }
        
        if (user==null || !passwordEncoder.matches(
                loginRequestDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid password or email");
        }
        
        if (!user.getIsConfirmed()) {
            user.setOtpCode(generateOtpCode());
            user.setOtpCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            user = userRepository.save(user);
            sendConfirmationMessage(user , "Please, use this code to confirm your account");
            throw new AuthenticationException(
                    "Your account is not verified, we've sent a confirmation email");
        }
        
        if (!user.getIsActive()){
            throw new AuthorizationException("Your account has been suspended");
        }
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
        
        String accessToken = jwtUtil.generateToken(loginRequestDto.getEmail());
        
        return new LoginResponseDto(accessToken);
    }
    
    public MessageResponseDto resetPassword(EmailRequestDto emailRequestDto) {
        String successMessage = "We've sent a confirmation code to " +
                emailRequestDto.getEmail() +
                " if this email meets a record on our system";
        
        Optional<User> existUser = userRepository.findByEmail(emailRequestDto.getEmail());
        
        if(existUser.isEmpty()){
            return new MessageResponseDto(successMessage);
        }
        
        var user = existUser.get();
        user.setOtpCode(generateOtpCode());
        user.setOtpCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user = userRepository.save(user);
        
        sendConfirmationMessage(user, "Please, use this code to reset your password");
        
        return new MessageResponseDto(successMessage);
    }
    
    public MessageResponseDto resetPasswordConfirm(
            ResetPasswordConfirmRequestDto resetPasswordDto){
        
        if (!resetPasswordDto.getPassword().equals(
                resetPasswordDto.getPasswordAgain())) {
            throw new BadRequestException("The two password fields should be the same!");
        }
        
        Optional<User> existUser = userRepository.findByEmail(resetPasswordDto.getEmail());
        
        User user = null;
        boolean isOtpNotValid = true;
        if (existUser.isPresent()) {
            user = existUser.get();
            isOtpNotValid = user.isOtpNotValid(resetPasswordDto.getCode());
        }
        
        if (user==null || isOtpNotValid) {
            throw new AuthenticationException("Invalid confirmation code or email");
        }
        
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        user.setOtpCode(null);
        user.setOtpCodeExpiresAt(null);
        user = userRepository.save(user);
        
        return new MessageResponseDto("Your password has been changed successfully");
    }
    
    public MessageResponseDto sendConfirmationCode(EmailRequestDto emailRequestDto) {
        String successMessage = "We've sent a confirmation code to " +
                emailRequestDto.getEmail();
        
        Optional<User> existUser = userRepository.findByEmail(emailRequestDto.getEmail());
        
        if (existUser.isEmpty()) {
            return new MessageResponseDto(successMessage);
        }
        
        var user = existUser.get();
        user.setOtpCode(generateOtpCode());
        user.setOtpCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user = userRepository.save(user);
        
        sendConfirmationMessage(user, "Please use the confirmation code below");
        
        return new MessageResponseDto(successMessage);
    }
    
    public MessageResponseDto verifyConfirmationCode(ConfirmationCodeRequestDto confirmAccountDto) {
        
        Optional<User> existUser = userRepository.findByEmail(confirmAccountDto.getEmail());
        
        User user = null;
        boolean isOtpNotValid = true;
        if (existUser.isPresent()) {
            user = existUser.get();
            isOtpNotValid = user.isOtpNotValid(confirmAccountDto.getCode());
        }
        
        if (user==null || isOtpNotValid) {
            throw new AuthenticationException("Invalid confirmation code or email");
        }
        
        user.setOtpCode(null);
        user.setOtpCodeExpiresAt(null);
        user = userRepository.save(user);
        
        return new MessageResponseDto("Confirmation code verified successfully");
    }
    
    private void sendConfirmationMessage(User user, String message) {
        try {
            emailService.sendConfirmationMessage(user, message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void sendAlertMessage(User user, String message) {
        try {
            emailService.sendAlertMessage(user, message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String generateOtpCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        System.out.println("\nConfirmationCode: " + code);
        return String.valueOf(code);
    }
}
