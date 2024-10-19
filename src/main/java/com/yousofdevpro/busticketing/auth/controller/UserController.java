package com.yousofdevpro.busticketing.auth.controller;

import com.yousofdevpro.busticketing.auth.dto.UserRequestDto;
import com.yousofdevpro.busticketing.auth.dto.UserDtoResponse;
import com.yousofdevpro.busticketing.auth.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDtoResponse> createUser(
            @Validated @RequestBody UserRequestDto userRequestDto){
        var createdUser = userService.createUser(userRequestDto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserDtoResponse> getUsers() {
        return userService.getUsers();
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public List<UserDtoResponse> getUserById(@PathVariable Long id) {
        return userService.getUsers();
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserDtoResponse updateUserById(
            @PathVariable Long id,
            @Validated @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUserById(userRequestDto, id);
    }
}
