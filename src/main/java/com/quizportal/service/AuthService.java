package com.quizportal.service;

import com.quizportal.dto.request.LoginRequest;
import com.quizportal.dto.request.RegisterRequest;
import com.quizportal.dto.response.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);

}