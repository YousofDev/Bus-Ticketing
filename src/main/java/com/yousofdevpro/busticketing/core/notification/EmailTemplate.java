package com.yousofdevpro.busticketing.core.notification;

import com.yousofdevpro.busticketing.auth.model.User;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplate {
    
    public String getHtmlConfirmationMessage(User user, String message) {
        return "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h3 style=\"color: #333;\">Dear " + user.getFirstName() + ", </h3>"
                + "<p style=\"font-size: 14px;\">" + message + "</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<p style=\"font-size: 16px; text-decoration: none; font-weight: bold; color: #007bff;\">" + user.getOtpCode() + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        
        
    }
    
    public String getHtmlAlertMessage(User user, String message) {
        return "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h3 style=\"color: #333;\">Dear " + user.getFirstName() + ", </h3>"
                + "<p style=\"font-size: 14px;\">" + message + "</p>"
                + "</div>"
                + "</body>"
                + "</html>";
        
        
    }
    
}
