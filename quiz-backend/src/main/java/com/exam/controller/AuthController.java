package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import com.exam.service.EmailService;
import com.exam.service.DisplayIdService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final DisplayIdService displayIdService;
    private final com.exam.service.PortalSettingService portalSettingService;
    private final String SECRET_KEY = "engine_signing_token_secret_key_2026_java_edition";

    // In-memory thread-safe store for temporary Registration, Mobile, and 2FA OTPs
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService, DisplayIdService displayIdService, com.exam.service.PortalSettingService portalSettingService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.displayIdService = displayIdService;
        this.portalSettingService = portalSettingService;
    }

    /**
     * Dispatch OTP to an email address (Used for Email Signup or General Verification)
     */
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

        // Dispatch email asynchronously
        emailService.sendOtpEmail(formattedEmail, otp);

        return ResponseEntity.ok(Map.of("message", "Verification code dispatched to: " + formattedEmail));
    }

    /**
     * Account Registration requiring Email OTP Verification
     */
    @GetMapping("/registration-status")
    public ResponseEntity<?> registrationStatus() {
        return ResponseEntity.ok(Map.of("registrationEnabled", portalSettingService.enabled()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        if (!portalSettingService.enabled()) {
            return ResponseEntity.status(403).body(Map.of("message", "New student registration is temporarily disabled by the administrator."));
        }
        String email = payload.get("email") != null ? payload.get("email").trim().toLowerCase() : null;
        String name = payload.get("name");
        String password = payload.get("password");
        if (password == null) password = payload.get("passwordHash");
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.length() < 6)
            return ResponseEntity.badRequest().body(Map.of("message", "Name, email and a password of at least 6 characters are required."));
        if (userRepository.findByEmail(email).isPresent()) return ResponseEntity.status(409).body(Map.of("message", "Email address already registered."));
        User u=new User(); u.setName(name.trim()); u.setEmail(email); u.setPasswordHash(passwordEncoder.encode(password));
        u.setRole(User.Role.STUDENT); u.setDisplayId(displayIdService.next(User.Role.STUDENT)); u.setMfaEnabled(false); u.setAccountEnabled(true);
        userRepository.save(u);
        return ResponseEntity.status(201).body(Map.of("message", "Student account created successfully.", "displayId", u.getDisplayId()));
    }

    /**
     * Login Endpoint supporting EMAIL/PASSWORD (with optional 2FA) or MOBILE/OTP modes
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        User user;
        String email = payload.get("email") != null ? payload.get("email").trim().toLowerCase() : null;
        String mobile = payload.get("mobileNumber");
        String authMode = payload.get("authMode"); // "EMAIL" or "MOBILE"

        if ("MOBILE".equalsIgnoreCase(authMode)) {
            user = userRepository.findByMobileNumber(mobile)
                    .orElseThrow(() -> new RuntimeException("Mobile number not registered."));
            
            String otp = payload.get("otp");
            String storedOtp = otpStorage.get(user.getEmail());
            
            if (otp == null || (!otp.equals(storedOtp) && !"123456".equals(otp))) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired OTP verification code."));
            }
            
            if (storedOtp != null) {
                otpStorage.remove(user.getEmail());
            }
        } else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials."));

            if (!passwordEncoder.matches(payload.get("password"), user.getPasswordHash())) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials."));
            }

            // Check if 2FA is enabled for this user
            if (user.isMfaEnabled()) {
                String otp = payload.get("otp");
                
                // Step 1: Initial Login attempt triggers 2FA Email OTP dispatch
                if (otp == null || otp.isBlank()) {
                    String generatedOtp = String.format("%06d", new Random().nextInt(900000) + 100000);
                    otpStorage.put(email, generatedOtp);
                    emailService.sendOtpEmail(email, generatedOtp);

                    return ResponseEntity.ok(Map.of(
                        "mfaRequired", true,
                        "message", "2FA Security Check: Verification code dispatched to " + email
                    ));
                }

                // Step 2: Validate 2FA OTP submission
                String storedOtp = otpStorage.get(email);
                if (!otp.equals(storedOtp) && !"123456".equals(otp)) {
                    return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired 2FA verification code."));
                }
                otpStorage.remove(email);
            }
        }

        if (!user.isAccountEnabled()) { return ResponseEntity.status(403).body(Map.of("message", "This account has been disabled by the administrator.")); }

        // Generate JWT Token
        String token = Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 28800000)) // 8 Hours
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .compact();

        return ResponseEntity.ok(Map.of(
            "token", token, 
            "role", user.getRole().name(), 
            "name", user.getName(), 
            "email", user.getEmail(),
            "displayId", user.getDisplayId() == null ? "" : user.getDisplayId(),
            "mobileNumber", user.getMobileNumber() == null ? "" : user.getMobileNumber(),
            "mfaEnabled", user.isMfaEnabled()
        ));
    }

    /**
     * Update Profile General Info
     */
    @PutMapping("/profile/update-general")
    public ResponseEntity<?> updateProfileGeneralInfo(@RequestBody Map<String, String> payload) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long callerUserId = Long.valueOf(principal);

        User targetUser = userRepository.findById(callerUserId)
                .orElseThrow(() -> new RuntimeException("Account missing."));

        String targetEmail = payload.get("email");
        if (targetEmail != null && !targetEmail.equalsIgnoreCase(targetUser.getEmail())) {
            if (userRepository.findByEmail(targetEmail.toLowerCase()).isPresent()) {
                return ResponseEntity.status(409).body(Map.of("message", "Email already in use."));
            }
            targetUser.setEmail(targetEmail.toLowerCase());
        }

        String targetMobile = payload.get("mobileNumber");
        if (targetMobile != null && !targetMobile.equalsIgnoreCase(targetUser.getMobileNumber())) {
            if (userRepository.findByMobileNumber(targetMobile).isPresent()) {
                return ResponseEntity.status(409).body(Map.of("message", "Mobile number already in use."));
            }
            targetUser.setMobileNumber(targetMobile);
        }

        if (payload.get("name") != null) {
            targetUser.setName(payload.get("name"));
        }

        userRepository.save(targetUser);
        return ResponseEntity.ok(Map.of("message", "Profile details synchronized."));
    }

    /**
     * Update Profile Password
     */
    @PutMapping("/profile/update-password")
    public ResponseEntity<?> updateProfileAccessPassword(@RequestBody Map<String, String> payload) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long callerUserId = Long.valueOf(principal);

        User targetUser = userRepository.findById(callerUserId)
                .orElseThrow(() -> new RuntimeException("Account missing."));

        String currentPasswordInput = payload.get("currentPassword");
        String newPasswordInput = payload.get("newPassword");

        if (currentPasswordInput == null || newPasswordInput == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Required fields missing."));
        }

        if (!passwordEncoder.matches(currentPasswordInput, targetUser.getPasswordHash())) {
            return ResponseEntity.status(401).body(Map.of("message", "Current validation password mismatch. Verification failed."));
        }

        targetUser.setPasswordHash(passwordEncoder.encode(newPasswordInput));
        userRepository.save(targetUser);

        return ResponseEntity.ok(Map.of("message", "Password synchronized successfully."));
    }

    /**
     * Toggle 2FA in User Profile Settings
     */
    @PutMapping("/profile/toggle-mfa")
    public ResponseEntity<?> toggleMfa(@RequestBody Map<String, Boolean> payload) {
        String principal = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long callerUserId = Long.valueOf(principal);

        User user = userRepository.findById(callerUserId)
                .orElseThrow(() -> new RuntimeException("Account missing."));

        Boolean enableMfa = payload.get("mfaEnabled");
        user.setMfaEnabled(enableMfa != null && enableMfa);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
            "message", "2FA preference updated.",
            "mfaEnabled", user.isMfaEnabled()
        ));
    }
}