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
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public ProfileController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @PutMapping("/update-general")
    public ResponseEntity<?> updateGeneral(Authentication auth, @RequestBody Map<String,String> body) {
        User user = users.findByEmail(auth.getName()).orElseThrow();
        String name = body.get("name");
        String email = body.get("email");
        if (name != null && !name.isBlank()) user.setName(name.trim());
        if (email != null && !email.isBlank()) user.setEmail(email.trim());
        users.save(user);
        return ResponseEntity.ok(Map.of("message","Profile updated successfully"));
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(Authentication auth, @RequestBody Map<String,String> body) {
        User user = users.findByEmail(auth.getName()).orElseThrow();
        String current = body.get("currentPassword");
        String next = body.get("newPassword");
        if (current == null || next == null || next.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("message","Current password and a new password of at least 6 characters are required."));
        if (!encoder.matches(current, user.getPasswordHash()))
            return ResponseEntity.status(400).body(Map.of("message","Current password is incorrect."));
        user.setPasswordHash(encoder.encode(next));
        users.save(user);
        return ResponseEntity.ok(Map.of("message","Password changed successfully"));
    }
}
