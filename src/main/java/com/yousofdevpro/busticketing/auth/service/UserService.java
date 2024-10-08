package com.yousofdevpro.busticketing.auth.service;

import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private UserRepository userRepository;
    
    
    
}
