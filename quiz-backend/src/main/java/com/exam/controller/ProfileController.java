package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =====================================================
    // CHANGE PASSWORD AFTER LOGIN
    // Works for both STUDENT and ADMIN
    // =====================================================

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(
            Authentication authentication,
            @RequestBody Map<String, String> request
    ) {

        if (authentication == null ||
                authentication.getName() == null) {

            return ResponseEntity
                    .status(401)
                    .body(
                            Map.of(
                                    "message",
                                    "You are not authenticated."
                            )
                    );
        }

        String currentPassword =
                request.get("currentPassword");

        String newPassword =
                request.get("newPassword");

        if (currentPassword == null ||
                currentPassword.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Current password is required."
                            )
                    );
        }

        if (newPassword == null ||
                newPassword.length() < 6) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "New password must contain at least 6 characters."
                            )
                    );
        }

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(404)
                    .body(
                            Map.of(
                                    "message",
                                    "User account not found."
                            )
                    );
        }

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPasswordHash()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "Current password is incorrect."
                            )
                    );
        }

        // Prevent setting the same password again
        if (passwordEncoder.matches(
                newPassword,
                user.getPasswordHash()
        )) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "message",
                                    "New password must be different from your current password."
                            )
                    );
        }

        user.setPasswordHash(
                passwordEncoder.encode(
                        newPassword
                )
        );

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Password changed successfully."
                )
        );
    }
}
