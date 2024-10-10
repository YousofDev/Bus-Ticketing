package com.yousofdevpro.busticketing.reservation.service;

import com.yousofdevpro.busticketing.auth.repository.UserRepository;
import com.yousofdevpro.busticketing.config.exception.BadRequestException;
import com.yousofdevpro.busticketing.config.exception.ResourceNotFoundException;
import com.yousofdevpro.busticketing.reservation.dto.PaymentRequestDto;
import com.yousofdevpro.busticketing.reservation.dto.PaymentResponseDto;
import com.yousofdevpro.busticketing.reservation.model.Payment;
import com.yousofdevpro.busticketing.reservation.model.PaymentStatus;
import com.yousofdevpro.busticketing.reservation.model.TicketStatus;
import com.yousofdevpro.busticketing.reservation.repository.PaymentRepository;
import com.yousofdevpro.busticketing.reservation.repository.TicketRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public PaymentResponseDto makePayment(PaymentRequestDto paymentRequestDto){
        
        var ticket = ticketRepository.findById(paymentRequestDto.getTicketId())
                .orElseThrow(()->new ResourceNotFoundException("Ticket not found"));
        
        if(ticket.getStatus().equals(TicketStatus.COMPLETED)){
            throw new BadRequestException("Ticket already paid");
        }
        
        if (ticket.getStatus().equals(TicketStatus.CANCELED)) {
            throw new BadRequestException("Payment isn't acceptable for a canceled ticket");
        }
        
        var customer = userRepository.getReferenceById(paymentRequestDto.getCustomerUserId());
        
        var payment = Payment.builder()
                .status(PaymentStatus.valueOf(paymentRequestDto.getPaymentStatus()))
                .amount(ticket.getPrice())
                .ticket(ticket)
                .customer(customer)
                .build();
        
        payment = paymentRepository.save(payment);
        
        if (paymentRequestDto.getPaymentStatus().equals(PaymentStatus.COMPLETED.name())){
            ticket.setStatus(TicketStatus.COMPLETED);
            ticketRepository.save(ticket);
        }
        
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .paymentStatus(payment.getStatus())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .ticketId(ticket.getId())
                .customerUserId(customer.getId())
                .build();
    }
}
