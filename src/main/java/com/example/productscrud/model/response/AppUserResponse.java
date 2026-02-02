package com.example.productscrud.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserResponse {
    private Long userId;
    private String fullName;
    private String email;
    private List<String> roles;
}
