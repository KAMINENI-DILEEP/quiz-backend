package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordResetController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        if (email == null || email.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and new password are required."));
        }

        User user = userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found with this email address."));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }
}
