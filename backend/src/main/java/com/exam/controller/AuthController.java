package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import com.exam.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    // In-memory or temporary storage map for signup OTPs
    private final Map<String, String> otpStorage = new HashMap<>();

    @PostMapping("/send-email-otp")
    public ResponseEntity<?> sendEmailOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email address is required."));
        }

        String formattedEmail = email.trim().toLowerCase();

        // Generate a random 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(900000) + 100000);
        otpStorage.put(formattedEmail, otp);

        // Dispatch email using the reliable sendEmail method
        String subject = "Account Registration Verification Code";
        String body = "Your One-Time Password (OTP) for account registration is: " + otp + "\n\nThis code will expire in 5 minutes.";
        
        emailService.sendEmail(formattedEmail, subject, body);

        return ResponseEntity.ok(Map.of("message", "Verification code dispatched to: " + formattedEmail));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String name = request.get("name");
        String password = request.get("password");
        String mobileNumber = request.get("mobileNumber");
        String roleStr = request.get("role");

        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and OTP are required."));
        }

        String formattedEmail = email.trim().toLowerCase();
        String storedOtp = otpStorage.get(formattedEmail);

        if (storedOtp == null || !storedOtp.equals(otp)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid or expired verification code."));
        }

        if (userRepository.findByEmail(formattedEmail).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email is already registered."));
        }

        User newUser = new User();
        newUser.setName(name != null ? name : "User");
        newUser.setEmail(formattedEmail);
        newUser.setMobileNumber(mobileNumber);
        newUser.setPasswordHash(passwordEncoder.encode(password));
        newUser.setRole("ADMIN".equalsIgnoreCase(roleStr) ? User.Role.ADMIN : User.Role.STUDENT);
        newUser.setMfaEnabled(false);

        userRepository.save(newUser);
        otpStorage.remove(formattedEmail);

        return ResponseEntity.ok(Map.of("message", "Account registered successfully. Please sign in."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            // Return token or session success response mapping
            Map<String, Object> response = new HashMap<>();
            response.append("message", "Login successful");
            response.put("email", user.getEmail());
            response.put("role", user.getRole().name());
            response.put("name", user.getName());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password."));
        }
    }
}
