package com.yousofdevpro.busticketing.core.notification;

import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.reservation.dto.response.TicketDetailsResponseDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender emailSender;
    private final EmailTemplate template;
    
    @Async
    public void sendEmail(String to, String subject, String text) {
        try{
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            emailSender.send(message);
        }catch (Exception e){
            System.out.println(e.getLocalizedMessage());
        }
    }
    
    public void sendConfirmationMessage(User user, String message) throws MessagingException {
        String subject = "Confirmation code from BusTic";
        String htmlMessage = template.getHtmlConfirmationMessage(user, message);
        this.sendEmail(user.getEmail(), subject, htmlMessage);
    }
    
    public void sendAlertMessage(User user, String message) throws MessagingException {
        String subject = "Alert from BusTic";
        String htmlMessage = template.getHtmlAlertMessage(user, message);
        this.sendEmail(user.getEmail(), subject, htmlMessage);
    }
    
    public void sendTicketMessage(TicketDetailsResponseDto ticket) throws MessagingException {
        String subject = "Your Ticket from BusTic";
        String htmlMessage = template.getHtmTicketMessage(ticket);
        this.sendEmail(ticket.getCustomerEmail(), subject, htmlMessage);
    }
    
    
    
}
