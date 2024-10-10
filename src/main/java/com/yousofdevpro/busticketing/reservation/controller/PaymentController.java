package com.yousofdevpro.busticketing.reservation.controller;

import com.yousofdevpro.busticketing.reservation.dto.PaymentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.PaymentResponseDto;
import com.yousofdevpro.busticketing.reservation.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments")
@AllArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PaymentResponseDto> makePayment(
            @Validated @RequestBody PaymentRequestDto paymentRequestDto){
        
        var createdPayment = paymentService.makePayment(paymentRequestDto);
        
        return new ResponseEntity<>(createdPayment, HttpStatus.CREATED);
    }
}
