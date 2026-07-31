package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import com.exam.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom =
            new SecureRandom();

    // =====================================================
    // FORGOT PASSWORD
    // =====================================================

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> request
    ) {

        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Email address is required."
                            )
                    );
        }

        email = email.trim().toLowerCase();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Email address not found."
                            )
                    );
        }

        // Generate secure 6 digit OTP

        int otpNumber =
                100000 +
                secureRandom.nextInt(900000);

        String otp =
                String.valueOf(otpNumber);

        // Store OTP

        user.setResetOtp(otp);

        // OTP valid for 5 minutes

        user.setResetOtpExpiry(
                LocalDateTime.now()
                        .plusMinutes(5)
        );

        userRepository.save(user);

        // Send OTP email

        emailService.sendOtpEmail(
                email,
                otp
        );

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset code sent to your email."
                )
        );
    }

    // =====================================================
    // RESET PASSWORD
    // =====================================================

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> request
    ) {

        String email =
                request.get("email");

        String otp =
                request.get("otp");

        String newPassword =
                request.get("newPassword");

        if (
                email == null ||
                otp == null ||
                newPassword == null
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Email, verification code and new password are required."
                            )
                    );
        }

        email =
                email.trim().toLowerCase();

        otp =
                otp.trim();

        // Password validation

        if (newPassword.length() < 6) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Password must contain at least 6 characters."
                            )
                    );
        }

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid email or verification code."
                            )
                    );
        }

        // Check OTP

        if (
                user.getResetOtp() == null ||
                !otp.equals(
                        user.getResetOtp()
                )
        ) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Invalid verification code."
                            )
                    );
        }

        // Check OTP expiration

        if (
                user.getResetOtpExpiry() == null ||
                user.getResetOtpExpiry()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            user.setResetOtp(null);
            user.setResetOtpExpiry(null);

            userRepository.save(user);

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Verification code has expired. Please request a new code."
                            )
                    );
        }

        // Encode and save new password

        user.setPasswordHash(
                passwordEncoder.encode(
                        newPassword
                )
        );

        // OTP can only be used once

        user.setResetOtp(null);
        user.setResetOtpExpiry(null);

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password reset successfully. Please sign in."
                )
        );
    }
}
