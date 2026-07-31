package com.exam.controller;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import com.exam.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        if (user.getEmail() == null || user.getPasswordHash() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email and password are required"
                    ));
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email is already in use"
                    ));
        }

        user.setPasswordHash(
                passwordEncoder.encode(user.getPasswordHash())
        );

        /*
         * IMPORTANT:
         * Public registration must ALWAYS create STUDENT accounts.
         * Never accept ADMIN role from frontend registration.
         */
        user.setRole(User.Role.STUDENT);

        userRepository.save(user);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "User registered successfully"
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> request
    ) {

        String email = request.get("email");
        String password = request.get("password");

        if (email == null ||
                password == null ||
                email.isBlank() ||
                password.isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Email and password are required"
                    ));
        }

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    email,
                                    password
                            )
                    );

            if (!authentication.isAuthenticated()) {
                return ResponseEntity.status(401)
                        .body(Map.of(
                                "message",
                                "Authentication failed"
                        ));
            }

            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "User not found"
                            )
                    );

            /*
             * Token contains:
             *
             * subject = email
             * role    = ADMIN / STUDENT
             */
            String token =
                    jwtUtil.generateToken(
                            user.getEmail(),
                            user.getRole().name()
                    );

            Map<String, Object> response =
                    new HashMap<>();

            response.put("token", token);

            response.put(
                    "role",
                    user.getRole().name()
            );

            response.put(
                    "email",
                    user.getEmail()
            );

            response.put(
                    "name",
                    user.getName() != null
                            ? user.getName()
                            : "User"
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.status(401)
                    .body(Map.of(
                            "message",
                            "Invalid email or password"
                    ));
        }
    }
}
