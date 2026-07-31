package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminAccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAccountController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(user -> Map.<String, Object>of(
                        "userId", user.getUserId(),
                        "name", user.getName() == null ? "" : user.getName(),
                        "email", user.getEmail() == null ? "" : user.getEmail(),
                        "role", user.getRole() == null ? "" : user.getRole().name(),
                        "createdAt", user.getCreatedAt() == null ? "" : user.getCreatedAt().toString(),
                        "passwordStatus", "Secured"
                ))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        String email = body.get("email");
        String password = body.get("password");

        if (name == null || name.isBlank() || email == null || email.isBlank()
                || password == null || password.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Name, email and a password of at least 6 characters are required."
            ));
        }

        email = email.trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Email already registered."));
        }

        User admin = new User();
        admin.setName(name.trim());
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(User.Role.ADMIN);
        admin.setMfaEnabled(false);
        userRepository.save(admin);

        return ResponseEntity.status(201).body(Map.of("message", "Administrator created successfully."));
    }

    @PutMapping("/users/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters."));
        }

        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "User not found."));
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully."));
    }
}
