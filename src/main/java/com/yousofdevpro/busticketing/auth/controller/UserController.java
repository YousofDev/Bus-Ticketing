package com.yousofdevpro.busticketing.auth.controller;

import com.yousofdevpro.busticketing.auth.dto.UserRequestDto;
import com.yousofdevpro.busticketing.auth.dto.UserDtoResponse;
import com.yousofdevpro.busticketing.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createUser(
            @Validated @RequestBody UserRequestDto userRequestDto){
        userService.createUser(userRequestDto);
        return ResponseEntity.accepted().build();
    }
    
    @GetMapping
    public List<UserDtoResponse> getUsers() {
        return userService.getUsers();
    }
    
    @GetMapping("/{id}")
    public List<UserDtoResponse> getUserById(@PathVariable Long id) {
        return userService.getUsers();
    }
    
    @PutMapping("/{id}")
    public UserDtoResponse updateUserById(
            @PathVariable Long id,
            @Validated @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUserById(userRequestDto, id);
    }
}
