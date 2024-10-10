package com.yousofdevpro.busticketing.reservation.dto;

import com.yousofdevpro.busticketing.config.exception.InEnum;
import com.yousofdevpro.busticketing.reservation.model.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentRequestDto {
    
    @NotBlank(message = "Payment status is required")
    @InEnum(value = PaymentStatus.class, message = "Payment status should be a value in: PENDING, COMPLETED, FAILED")
    private String paymentStatus;
    
    @NotNull(message = "Ticket id is required")
    private Long ticketId;
    
    @NotNull(message = "Customer user id is required")
    private Long customerUserId;
    
    @NotBlank(message = "Payment description is required")
    private String description;
}
