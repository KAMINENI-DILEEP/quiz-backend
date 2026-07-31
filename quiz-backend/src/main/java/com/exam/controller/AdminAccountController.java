package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import com.exam.service.DisplayIdService;
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
    private final DisplayIdService displayIdService;
    private final com.exam.service.PortalSettingService portalSettingService;

    public AdminAccountController(UserRepository userRepository, PasswordEncoder passwordEncoder, DisplayIdService displayIdService, com.exam.service.PortalSettingService portalSettingService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.displayIdService = displayIdService;
        this.portalSettingService = portalSettingService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .sorted(Comparator.comparing(User::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(user -> Map.<String, Object>of(
                        "userId", user.getUserId(),
                        "displayId", user.getDisplayId() == null ? "" : user.getDisplayId(),
                        "name", user.getName() == null ? "" : user.getName(),
                        "email", user.getEmail() == null ? "" : user.getEmail(),
                        "role", user.getRole() == null ? "" : user.getRole().name(),
                        "createdAt", user.getCreatedAt() == null ? "" : user.getCreatedAt().toString(),
                        "passwordStatus", "Secured",
                        "accountEnabled", user.isAccountEnabled()
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
        admin.setDisplayId(displayIdService.next(User.Role.ADMIN));
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

    @GetMapping("/student-portal/settings")
    public ResponseEntity<?> portalSettings(){ return ResponseEntity.ok(Map.of("registrationEnabled", portalSettingService.enabled())); }

    @PutMapping("/student-portal/settings")
    public ResponseEntity<?> updatePortalSettings(@RequestBody Map<String, Boolean> body){
        boolean enabled=Boolean.TRUE.equals(body.get("registrationEnabled")); portalSettingService.set(enabled);
        return ResponseEntity.ok(Map.of("registrationEnabled", enabled, "message", enabled ? "Public student registration enabled." : "Public student registration stopped."));
    }

    @PostMapping("/students")
    public ResponseEntity<?> createStudent(@RequestBody Map<String,String> body){
        String name=body.get("name"), email=body.get("email"), password=body.get("password");
        if(name==null||name.isBlank()||email==null||email.isBlank()||password==null||password.length()<6) return ResponseEntity.badRequest().body(Map.of("message","Name, email and password of at least 6 characters are required."));
        email=email.trim().toLowerCase(); if(userRepository.findByEmail(email).isPresent()) return ResponseEntity.status(409).body(Map.of("message","Email already registered."));
        User u=new User(); u.setName(name.trim()); u.setEmail(email); u.setPasswordHash(passwordEncoder.encode(password)); u.setRole(User.Role.STUDENT); u.setDisplayId(displayIdService.next(User.Role.STUDENT)); u.setAccountEnabled(true); userRepository.save(u);
        return ResponseEntity.status(201).body(Map.of("message","Student created successfully.","displayId",u.getDisplayId()));
    }

    @PutMapping("/users/{id}/enabled")
    public ResponseEntity<?> setUserEnabled(@PathVariable Long id,@RequestBody Map<String,Boolean> body){ User u=userRepository.findById(id).orElse(null); if(u==null)return ResponseEntity.notFound().build(); u.setAccountEnabled(Boolean.TRUE.equals(body.get("enabled"))); userRepository.save(u); return ResponseEntity.ok(Map.of("message","Account status updated.","enabled",u.isAccountEnabled())); }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id){ User u=userRepository.findById(id).orElse(null); if(u==null)return ResponseEntity.notFound().build(); if(u.getRole()==User.Role.ADMIN) return ResponseEntity.badRequest().body(Map.of("message","Administrator accounts cannot be deleted from Student Portal Control.")); userRepository.delete(u); return ResponseEntity.ok(Map.of("message","Student account deleted.")); }
}
