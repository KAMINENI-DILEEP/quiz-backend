package com.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MfaService {

    @Autowired
    private EmailService emailService;

    public void sendMfaCode(String toEmail, String code) {
        String subject = "Your Multi-Factor Authentication Code";
        String body = "Your secure MFA login code is: " + code + "\n\nThis code will expire shortly.";
        
        emailService.sendEmail(toEmail, subject, body);
    }
}
