package com.yousofdevpro.busticketing.auth.service;

import com.yousofdevpro.busticketing.auth.dto.*;
import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.core.exception.AuthenticationException;
import com.yousofdevpro.busticketing.core.exception.AuthorizationException;
import com.yousofdevpro.busticketing.core.exception.BadRequestException;
import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import com.yousofdevpro.busticketing.core.notification.EmailService;
import com.yousofdevpro.busticketing.core.security.JwtUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final static Logger logger = Logger.getLogger(AuthService.class.getName());
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    
    @Transactional
    public MessageResponseDto register(RegisterRequestDto registerRequestDto) {
        
        if (!registerRequestDto.getPassword().equals(
                registerRequestDto.getPasswordAgain())) {
            throw new BadRequestException("password and passwordAgain must match!");
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
        
        sendConfirmationMessage(user, "Please, use this code to confirm your account");
        
        return new MessageResponseDto(successMessage);
    }
    
    @Transactional
    public MessageResponseDto confirmAccount(ConfirmationCodeRequestDto confirmAccountDto) {
        
        var user = userRepository.findByEmail(confirmAccountDto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid confirmation code or email"));
        
        if (user.isOtpNotValid(confirmAccountDto.getCode())) {
            throw new AuthenticationException("Invalid confirmation code or email");
        }
        
        user.setIsConfirmed(true);
        user.setOtpCode(null);
        user.setOtpCodeExpiresAt(null);
        user.setCreatedBy(user.getId());
        user.setUpdatedBy(user.getId());
        user = userRepository.save(user);
        
        return new MessageResponseDto("Your account has been confirmed successfully");
    }
    
    public TokensResponseDto login(LoginRequestDto loginRequestDto) {
        
        var user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid password or email"));
        
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid password or email");
        }
        
        if (!user.getIsConfirmed()) {
            user.setOtpCode(generateOtpCode());
            user.setOtpCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            user = userRepository.save(user);
            sendConfirmationMessage(user, "Please, use this code to confirm your account");
            throw new AuthenticationException(
                    "Your account is not verified, we've sent a confirmation email");
        }
        
        if (!user.getIsActive()) {
            throw new AuthorizationException("Your account has been suspended");
        }
        
        String accessToken = jwtUtil.generateAccessToken(loginRequestDto.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(loginRequestDto.getEmail());
        
        return new TokensResponseDto(accessToken, refreshToken);
    }
    
    @Transactional
    public MessageResponseDto resetPassword(EmailRequestDto emailRequestDto) {
        String successMessage = "We've sent a confirmation code to " +
                emailRequestDto.getEmail() +
                " if this email meets a record on our system";
        
        Optional<User> existUser = userRepository.findByEmail(emailRequestDto.getEmail());
        
        if (existUser.isEmpty()) {
            return new MessageResponseDto(successMessage);
        }
        
        var user = existUser.get();
        user.setOtpCode(generateOtpCode());
        user.setOtpCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user = userRepository.save(user);
        
        sendConfirmationMessage(user, "Please, use this code to reset your password");
        
        return new MessageResponseDto(successMessage);
    }
    
    @Transactional
    public MessageResponseDto resetPasswordConfirm(
            ResetPasswordConfirmRequestDto resetPasswordDto) {
        
        if (!resetPasswordDto.getPassword().equals(
                resetPasswordDto.getPasswordAgain())) {
            throw new BadRequestException("password and passwordAgain must match!");
        }
        
        var user = userRepository.findByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid confirmation code or email"));
        
        if (user.isOtpNotValid(resetPasswordDto.getCode())) {
            throw new AuthenticationException("Invalid confirmation code or email");
        }
        
        user.setPassword(passwordEncoder.encode(resetPasswordDto.getPassword()));
        user.setOtpCode(null);
        user.setOtpCodeExpiresAt(null);
        user = userRepository.save(user);
        
        return new MessageResponseDto("Your password has been changed successfully");
    }
    
    @Transactional
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
    
    @Transactional
    public MessageResponseDto verifyConfirmationCode(ConfirmationCodeRequestDto confirmAccountDto) {
        
        var user = userRepository.findByEmail(confirmAccountDto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid confirmation code or email"));
        
        if (user.isOtpNotValid(confirmAccountDto.getCode())) {
            throw new AuthenticationException("Invalid confirmation code or email");
        }
        
        user.setOtpCode(null);
        user.setOtpCodeExpiresAt(null);
        user = userRepository.save(user);
        
        return new MessageResponseDto("Confirmation code verified successfully");
    }
    
    public UserDtoResponse getUserProfile(UserDetails userDetails) {
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Can't get user profile"));
        return mapToUserDtoResponse(user);
    }
    
    @Transactional
    public UserDtoResponse updateUserProfile(
            UserDetails userDetails, ProfileRequestDto profileRequestDto) {
        
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Can't get user profile"));
        
        user.setFirstName(profileRequestDto.getFirstName());
        user.setLastName(profileRequestDto.getLastName());
        user.setPhone(profileRequestDto.getPhone());
        user.setEmail(profileRequestDto.getEmail());
        user.setRole(Role.valueOf(profileRequestDto.getRole()));
        
        user = userRepository.save(user);
        
        return mapToUserDtoResponse(user);
    }
    
    @Transactional
    public void changePassword(UserDetails userDetails,
                               ChangePasswordRequestDto changePasswordRequestDto) {
        
        if (!changePasswordRequestDto.getPassword().equals(
                changePasswordRequestDto.getPasswordAgain())) {
            throw new BadRequestException("password and password again must match!");
        }
        
        var user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new NotFoundException("Can't get user"));
        
        if (!passwordEncoder.matches(
                changePasswordRequestDto.getCurrentPassword(),
                user.getPassword())) {
            throw new AuthenticationException("current password is wrong!");
        }
        
        user.setPassword(passwordEncoder.encode(changePasswordRequestDto.getPassword()));
        userRepository.save(user);
    }
    
    public TokensResponseDto refreshToken(UserDetails userDetails) {
        String accessToken = jwtUtil.generateAccessToken(userDetails.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails.getUsername());
        return new TokensResponseDto(accessToken, refreshToken);
    }
    
    private UserDtoResponse mapToUserDtoResponse(User user) {
        return UserDtoResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
    
    private void sendConfirmationMessage(User user, String message) {
        logger.info("\nConfirmationCode: " + user.getOtpCode());
        try {
            emailService.sendConfirmationMessage(user, message);
        } catch (MessagingException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }
    
    private void sendAlertMessage(User user, String message) {
        try {
            emailService.sendAlertMessage(user, message);
        } catch (MessagingException e) {
            logger.warning(e.getLocalizedMessage());
        }
    }
    
    private String generateOtpCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        logger.info("\nConfirmationCode: " + code);
        return String.valueOf(code);
    }
    
}
