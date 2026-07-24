package com.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("dileepkamineni@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            System.out.println("SUCCESS: Email successfully dispatched to " + to);
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to send email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Optional: Keep your specific method if other parts of your app use it
    @Async
    public void sendOtpEmail(String recipientEmail, String otpCode) {
        sendEmail(recipientEmail, "Your Examination Portal Verification Code", 
            "Your One-Time Password (OTP) for authentication is: " + otpCode + 
            "\n\nThis code will expire in 5 minutes. Do not share this code with anyone.");
    }
}
