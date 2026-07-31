package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import com.exam.service.DisplayIdService;
import com.exam.service.PortalSettingService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DisplayIdService displayIdService;
    private final PortalSettingService portalSettingService;
    private final String SECRET_KEY = "engine_signing_token_secret_key_2026_java_edition";

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, DisplayIdService displayIdService, PortalSettingService portalSettingService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.displayIdService = displayIdService;
        this.portalSettingService = portalSettingService;
    }

    @GetMapping("/registration-status")
    public ResponseEntity<?> registrationStatus() {
        return ResponseEntity.ok(Map.of("registrationEnabled", portalSettingService.enabled()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        if (!portalSettingService.enabled()) return ResponseEntity.status(403).body(Map.of("message", "New student registration is temporarily disabled by the administrator."));
        String email = payload.get("email") == null ? null : payload.get("email").trim().toLowerCase();
        String name = payload.get("name");
        String password = payload.get("password");
        if (password == null) password = payload.get("passwordHash");
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("message", "Name, email and a password of at least 6 characters are required."));
        if (userRepository.findByEmail(email).isPresent()) return ResponseEntity.status(409).body(Map.of("message", "Email address already registered."));
        User u = new User();
        u.setName(name.trim()); u.setEmail(email); u.setPasswordHash(passwordEncoder.encode(password));
        u.setRole(User.Role.STUDENT); u.setDisplayId(displayIdService.next(User.Role.STUDENT)); u.setAccountEnabled(true);
        userRepository.save(u);
        return ResponseEntity.status(201).body(Map.of("message", "Student account created successfully.", "displayId", u.getDisplayId()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email") == null ? null : payload.get("email").trim().toLowerCase();
        String password = payload.get("password");
        if (email == null || password == null) return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials."));
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials."));
        if (!user.isAccountEnabled()) return ResponseEntity.status(403).body(Map.of("message", "This account has been disabled by the administrator."));
        String token = Jwts.builder().setSubject(user.getUserId().toString()).claim("role", user.getRole().name())
                .setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis() + 28800000))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8))).compact();
        return ResponseEntity.ok(Map.of("token", token, "role", user.getRole().name(), "name", user.getName(), "email", user.getEmail(), "displayId", user.getDisplayId() == null ? "" : user.getDisplayId()));
    }

    @PutMapping("/profile/update-general")
    public ResponseEntity<?> updateProfileGeneralInfo(@RequestBody Map<String, String> payload) {
        Long id = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Account missing."));
        String email = payload.get("email");
        if (email != null && !email.equalsIgnoreCase(user.getEmail())) {
            email = email.trim().toLowerCase();
            if (userRepository.findByEmail(email).isPresent()) return ResponseEntity.status(409).body(Map.of("message", "Email already in use."));
            user.setEmail(email);
        }
        if (payload.get("name") != null && !payload.get("name").isBlank()) user.setName(payload.get("name").trim());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Profile details synchronized."));
    }

    @PutMapping("/profile/update-password")
    public ResponseEntity<?> updateProfileAccessPassword(@RequestBody Map<String, String> payload) {
        Long id = Long.valueOf((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("Account missing."));
        String current = payload.get("currentPassword"), next = payload.get("newPassword");
        if (current == null || next == null || next.length() < 6) return ResponseEntity.badRequest().body(Map.of("message", "Current password and a new password of at least 6 characters are required."));
        if (!passwordEncoder.matches(current, user.getPasswordHash())) return ResponseEntity.status(401).body(Map.of("message", "Current password is incorrect."));
        user.setPasswordHash(passwordEncoder.encode(next)); userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }
}
