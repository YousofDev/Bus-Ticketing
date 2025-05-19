package com.yousofdevpro.busticketing.core.notification;

import com.yousofdevpro.busticketing.auth.model.User;
import com.yousofdevpro.busticketing.reservation.dto.response.TicketDetailsResponseDto;
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
    
    public String getHtmTicketMessage(TicketDetailsResponseDto ticket) {
        var journey = ticket.getDeparturePoint() + "  ->  " + ticket.getDestinationPoint();
        var issueDate = ticket.getCreatedAt().toLocalDate().toString();
        var departureDate = ticket.getDepartureDate().toString();
        var departureTime = ticket.getDepartureTime().toString();
        var arrivalTime = ticket.getArrivalTime().toString();
        var seatNumber = ticket.getSeatNumber().toString();
        var busNumber = ticket.getBusNumber();
        var status = ticket.getTicketStatus().name();
        var serviceGrade = ticket.getServiceGrade().name();
        var price = ticket.getPrice().toString();
        
        var name = ticket.getCustomerFirstName() + " " + ticket.getCustomerLastName();
        var email = ticket.getCustomerEmail();
        var phone = ticket.getCustomerPhone();
        
        return "<html>"
                + "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h3 style=\"color: #333;\">Dear " + ticket.getCustomerFirstName() + ", </h3>"
                + "<p style=\"font-size: 14px;\">Here are your ticket details:</p>"
                
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                
                + "<h1 style=\"text-align: center; font-weight: bold; margin: 0;\"><span style=\"color: skyblue;\">Bus</span>Tick</h1>"
                + "<hr style=\"border: none; border-top: 1.5px dashed #ccc; margin: 10px 0; height: 2px;\" />"
                
                + createDetailLine("Journey:", journey)
                + createDetailLine("Issue Date:", issueDate)
                + createDetailLine("Departure Date:", departureDate)
                + createDetailLine("Departure Time:", departureTime)
                + createDetailLine("Arrival Time:", arrivalTime)
                + createDetailLine("Seat Number:", seatNumber)
                + createDetailLine("Bus Number:", busNumber)
                + createDetailLine("Grade:", serviceGrade)
                + createDetailLine("Price:", price)
                + createDetailLine("Status:", status)
                + createDetailLine("Name:", name)
                + createDetailLine("Phone:", phone)
                
                + "<footer style=\"text-align: center; margin-top: 20px; font-size: 12px; color: #777;\">"
                + "Thank you for choosing BusTick! We wish you a pleasant journey."
                + "</footer>"
                
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
    
    private String createDetailLine(String label, String value) {
        return "<div style=\"display: flex; justify-content: space-between; margin-bottom: 10px;\">"
                + "<span style=\"font-size: 14px; font-weight: bold;\">"
                + label
                + "</span>"
                + "<span style=\"font-size: 14px; margin-left: 10px;\">"
                + value
                + "</span>"
                + "</div>"
                + "<hr style=\"border: none; border-top: 1.5px dashed #ccc; margin: 10px 0; height: 2px;\" />";
    }
}
