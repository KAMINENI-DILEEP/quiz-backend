package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import com.exam.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email address not found."));
        }

        // Generate a secure 6-digit random code
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setResetOtp(otp);
        userRepository.save(user);

        // Dispatch the generated OTP dynamically via EmailService
        String subject = "Password Recovery Verification Code";
        String body = "Hello " + user.getName() + ",\n\nYour 6-digit password reset code is: " + otp + "\n\nThis code will allow you to update your credentials securely.";
        
        emailService.sendEmail(email, subject, body);

        return ResponseEntity.ok(Map.of("message", "Password reset code sent successfully to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || user.getResetOtp() == null || !user.getResetOtp().equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or verification code."));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setResetOtp(null); // Clear OTP after successful use
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password reset successfully. Please sign in with your new password."));
    }
}
