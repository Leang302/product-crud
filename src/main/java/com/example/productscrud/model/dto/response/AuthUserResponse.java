package com.example.productscrud.model.dto.response;

import com.example.productscrud.model.enumeration.AppUserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Builder
@Getter
public class AuthUserResponse {
    private UUID userId;
    private String username;
    private Set<AppUserRole> roles;
}
