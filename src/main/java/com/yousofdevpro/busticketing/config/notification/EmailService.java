package com.yousofdevpro.busticketing.config.notification;

import com.yousofdevpro.busticketing.auth.model.User;
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
    public void sendEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        emailSender.send(message);
    }
    
    public void sendConfirmationLink(User user, String link) throws MessagingException {
        String subject = "Confirm your account at Spring";
        String htmlMessage = template.getHtmlConfirm(user, link);
        this.sendEmail(user.getEmail(), subject, htmlMessage);
    }
    
    public void sendResetPasswordLink(User user, String link) throws MessagingException {
        String subject = "Reset your password at Spring";
        String htmlMessage = template.getHtmlReset(user, link);
        this.sendEmail(user.getEmail(), subject, htmlMessage);
    }
    
    public void sendOtpMessage(User user, String token) throws MessagingException {
        String subject = "Get Verification Code";
        String htmlMessage = template.getHtmlOtp(user, token);
        this.sendEmail(user.getEmail(), subject, htmlMessage);
        
    }
}
