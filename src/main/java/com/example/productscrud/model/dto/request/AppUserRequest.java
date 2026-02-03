package com.example.productscrud.model.dto.request;

import com.example.productscrud.model.entity.AppUser;
import com.example.productscrud.model.enumeration.AppUserRole;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;

    @NotEmpty(message = "At least one role must be specified")
    private List<@NotNull(message = "Role cannot be null") AppUserRole> roles;

    public AppUser toEntity() {
        return AppUser.builder()
                .username(this.username)
                .password(this.password)           // ← plain text → must encode later!
                .roles(Set.copyOf(this.roles))     // List → Set (common for roles)
                .build();
    }
}