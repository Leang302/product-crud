package com.example.productscrud.controller;

import com.example.productscrud.model.dto.request.AppUserRequest;
import com.example.productscrud.model.dto.request.AuthRequest;
import com.example.productscrud.model.dto.response.AuthResponse;
import com.example.productscrud.model.enumeration.code.AuthResponseCode;
import com.example.productscrud.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        AuthResponse authResponse = authService.login(request);
        return responseEntity(AuthResponseCode.LOGIN_SUCCESS, authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AppUserRequest request) {
        AuthResponse authResponse = authService.register(request);
        return responseEntity(
                AuthResponseCode.USER_CREATED,
                authResponse
        );
    }
}