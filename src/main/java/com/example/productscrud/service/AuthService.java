package com.example.productscrud.service;

import com.example.productscrud.model.dto.request.AppUserRequest;
import com.example.productscrud.model.dto.request.AuthRequest;
import com.example.productscrud.model.dto.response.AuthResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AuthService extends UserDetailsService {

    AuthResponse login(AuthRequest request);

    AuthResponse register(AppUserRequest request);
}