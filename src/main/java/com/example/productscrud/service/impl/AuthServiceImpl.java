package com.example.productscrud.service.impl;

import com.example.productscrud.exception.ApiException;
import com.example.productscrud.jwt.JwtService;
import com.example.productscrud.model.dto.request.AppUserRequest;
import com.example.productscrud.model.dto.request.AuthRequest;
import com.example.productscrud.model.dto.response.AuthResponse;
import com.example.productscrud.model.dto.response.AuthUserResponse;
import com.example.productscrud.model.entity.AppUser;
import com.example.productscrud.model.enumeration.code.AuthResponseCode;
import com.example.productscrud.repository.AppUserRepository;
import com.example.productscrud.service.AuthService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

        private final AppUserRepository appUserRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuthenticationManager authenticationManager;

        public AuthServiceImpl(AppUserRepository appUserRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        @Lazy AuthenticationManager authenticationManager) {
                this.appUserRepository = appUserRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.authenticationManager = authenticationManager;
        }

        @Override
        public AuthResponse login(AuthRequest request) {
                try {
                        authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(
                                                        request.getUsername(),
                                                        request.getPassword()));
                } catch (BadCredentialsException e) {
                        throw new ApiException(AuthResponseCode.LOGIN_FAILED);
                }

                AppUser user = appUserRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> new UsernameNotFoundException(
                                                "User not found: " + request.getUsername()));

                if (!user.isEnabled()) {
                        throw new ApiException(AuthResponseCode.ACCOUNT_INACTIVE);
                }

                String accessToken = jwtService.generateToken(user);

                AuthUserResponse userInfo = AuthUserResponse.builder()
                                .userId(user.getUserId())
                                .username(user.getUsername())
                                .roles(user.getRoles() != null ? new HashSet<>(user.getRoles()) : new HashSet<>())
                                .build();

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .tokenType("Bearer")
                                .user(userInfo)
                                .build();
        }

        @Override
        public AuthResponse register(AppUserRequest request) {
                if (appUserRepository.existsByUsername(request.getUsername())) {
                        throw new ApiException(AuthResponseCode.USERNAME_ALREADY_EXISTS);
                }

                AppUser user = AppUser.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .roles(new HashSet<>(
                                                request.getRoles() != null ? request.getRoles() : Set.of("ROLE_USER")))
                                .build();

                AppUser savedUser = appUserRepository.save(user);

                String accessToken = jwtService.generateToken(savedUser);

                AuthUserResponse userInfo = AuthUserResponse.builder()
                                .userId(savedUser.getUserId())
                                .username(savedUser.getUsername())
                                .roles(savedUser.getRoles())
                                .build();

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .tokenType("Bearer")
                                .user(userInfo)
                                .build();
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return appUserRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        }
}