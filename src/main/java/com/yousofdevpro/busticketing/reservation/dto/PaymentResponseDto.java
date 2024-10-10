package com.yousofdevpro.busticketing.reservation.dto;

import com.yousofdevpro.busticketing.reservation.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PaymentResponseDto {
    Long id;
    private PaymentStatus paymentStatus;
    private BigDecimal amount;
    private Long customerUserId;
    private Long ticketId;
    private String description;
}
