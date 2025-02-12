package com.saas.dental_clinic.service;

import com.saas.dental_clinic.dto.LoginRequest;
import com.saas.dental_clinic.dto.LoginResponse;
import com.saas.dental_clinic.dto.RegisterRequest;

public interface AuthenticationService {
    LoginResponse authenticateUser(LoginRequest loginRequest);
    void registerUser(RegisterRequest signupRequest);
}