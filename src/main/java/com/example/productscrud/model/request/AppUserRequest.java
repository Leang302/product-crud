package com.example.productscrud.model.request;

import com.example.productscrud.model.entity.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserRequest {
    private String username;
    private String password;
    private List<String> roles;

    public AppUser toEntity() {
        return AppUser.builder()
                .username(username)
                .password(password)
                .build();
    }
}
