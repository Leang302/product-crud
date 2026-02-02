package com.example.productscrud.service;

import com.example.productscrud.model.request.AppUserRequest;
import com.example.productscrud.model.response.AppUserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface AppUserService extends UserDetailsService {
    AppUserResponse register(AppUserRequest request);
}
