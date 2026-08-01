package com.quizportal.service.impl;

import com.quizportal.dto.request.LoginRequest;
import com.quizportal.dto.request.RegisterRequest;
import com.quizportal.dto.response.LoginResponse;
import com.quizportal.entity.User;
import com.quizportal.enums.Role;
import com.quizportal.security.JwtService;
import com.quizportal.service.AuthService;
import com.quizportal.service.DisplayIdService;
import com.quizportal.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final DisplayIdService displayIdService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserService userService,
            DisplayIdService displayIdService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userService = userService;
        this.displayIdService = displayIdService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse register(RegisterRequest request) {

        if (userService.emailExists(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setDisplayId(displayIdService.generateStudentId());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setGender(request.getGender());
        user.setRole(Role.STUDENT);
        user.setAccountEnabled(true);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userService.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getDisplayId(),
                user.getName(),
                user.getRole().name());
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userService.findByEmail(request.getEmail());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new RuntimeException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(
                token,
                user.getDisplayId(),
                user.getName(),
                user.getRole().name());
    }
}