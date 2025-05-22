package com.yousofdevpro.busticketing.auth.service;

import com.yousofdevpro.busticketing.auth.dto.request.RegisterRequestDto;
import com.yousofdevpro.busticketing.auth.dto.request.UserRequestDto;
import com.yousofdevpro.busticketing.auth.dto.response.UserDtoResponse;
import com.yousofdevpro.busticketing.auth.model.Role;
import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.core.exception.ConflictException;
import com.yousofdevpro.busticketing.core.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public UserDtoResponse createUser(RegisterRequestDto registerRequestDto) {
        
        Optional<User> existUser = userRepository.findByEmail(registerRequestDto.getEmail());
        
        if (existUser.isPresent()) {
            throw new ConflictException("User already exists");
        }
        
        var user = User.builder()
                .firstName(registerRequestDto.getFirstName())
                .lastName(registerRequestDto.getLastName())
                .phone(registerRequestDto.getPhone())
                .email(registerRequestDto.getEmail())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .role(Role.valueOf(registerRequestDto.getRole()))
                .isConfirmed(true)
                .build();
        
        user = userRepository.save(user);
        
        return mapToUserDtoResponse(user);
    }
    
    public List<UserDtoResponse> getUsers() {
        return userRepository.findAllUsers();
    }
    
    public UserDtoResponse getUserById(Long id) {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
    
    public UserDtoResponse getUserByEmail(String email) {
        Optional<User> existUser = userRepository.findByEmail(email);
        return existUser.map(this::mapToUserDtoResponse).orElse(null);
    }
    
    @Transactional
    public UserDtoResponse updateUserById(UserRequestDto userRequestDto, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        user.setFirstName(userRequestDto.getFirstName());
        if(!userRequestDto.getLastName().isEmpty()){
            user.setLastName(userRequestDto.getLastName());
        }
        user.setEmail(user.getEmail());
        user.setPhone(userRequestDto.getPhone());
        user.setRole(Role.valueOf(userRequestDto.getRole()));
        
        user = userRepository.save(user);
        
        return mapToUserDtoResponse(user);
    }
    
    private UserDtoResponse mapToUserDtoResponse(User user){
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
    
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
