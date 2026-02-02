package com.example.productscrud.model.dto.response;

import lombok.*;

@Builder
@Getter
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private AuthUserResponse user;
}