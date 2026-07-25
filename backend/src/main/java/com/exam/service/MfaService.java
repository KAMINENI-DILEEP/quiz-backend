package com.exam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class MfaService {

    @Autowired
    private EmailService emailService;

    // Generate a 6-digit OTP code
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Send the OTP via email using Resend EmailService
    public void sendOtpEmail(String toEmail, String otpCode) {
        String subject = "Your Examination Portal MFA Verification Code";
        String body = "Your security verification code is: " + otpCode + "\nThis code will expire shortly.";
        
        emailService.sendEmail(toEmail, subject, body);
    }
}
