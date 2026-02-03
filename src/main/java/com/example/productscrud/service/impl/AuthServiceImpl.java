package com.example.productscrud.service.impl;

import com.example.productscrud.exception.ApiException;
import com.example.productscrud.jwt.JwtService;
import com.example.productscrud.model.dto.request.AppUserRequest;
import com.example.productscrud.model.dto.request.AuthRequest;
import com.example.productscrud.model.dto.response.AuthResponse;
import com.example.productscrud.model.dto.response.AuthUserResponse;
import com.example.productscrud.model.entity.AppUser;
import com.example.productscrud.model.enumeration.AppUserRole;
import com.example.productscrud.model.enumeration.code.AuthResponseCode;
import com.example.productscrud.repository.AppUserRepository;
import com.example.productscrud.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {
    @Value("${token.expires-in:3600}")
    private long jwtTokenValidity;

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
        String username = request.getUsername();

        // 1. Find user first (needed for lock check & increment)
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(AuthResponseCode.INVALID_CREDENTIALS));

        // 2. Auto-unlock if lock period has expired
        user.autoUnlockIfPossible();

        // 3. Check if still locked
        if (user.isLocked()) {
            long minutesLeft = (user.getAccountLockedUntil().getTime() - System.currentTimeMillis()) / 60000 + 1;
            throw new ApiException(
                    AuthResponseCode.ACCOUNT_LOCKED,
                    "Account is temporarily locked due to too many failed attempts. " +
                            "Please try again in approximately " + minutesLeft + " minute(s)."
            );
        }
        if (!user.isEnabled()) {  // = !user.isActive()
            throw new ApiException(
                    AuthResponseCode.ACCOUNT_INACTIVE,
                    "Your account has not been activated yet. Please wait for administrator approval."
            );
        }

        try {
            // 4. Attempt authentication (now safe — account is enabled)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            request.getPassword()
                    )
            );

            // 5. Success → reset failed attempts & lock
            user.resetFailedAttempts();
            appUserRepository.save(user);

        } catch (BadCredentialsException e) {
            // 6. Wrong password → increment attempts
            user.incrementFailedAttempts();

            int currentAttempts = user.getFailedLoginAttempts();

            if (currentAttempts >= 5) {
                // Lock for 30 minutes
                Date lockUntil = new Date(System.currentTimeMillis() + 30L * 60 * 1000);
                user.setAccountLockedUntil(lockUntil);
                appUserRepository.save(user);

                throw new ApiException(
                        AuthResponseCode.ACCOUNT_LOCKED,
                        "Too many failed login attempts. Account locked for 30 minutes."
                );
            } else {
                appUserRepository.save(user);
                throw new ApiException(
                        AuthResponseCode.INVALID_CREDENTIALS,
                        "Invalid username or password. Attempt " + currentAttempts + "/5."
                );
            }
        }

        // 7. Login successful
        String accessToken = jwtService.generateToken(user);

        AuthUserResponse userInfo = AuthUserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .roles(user.getRoles() != null ? new HashSet<>(user.getRoles()) : new HashSet<>())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .expiresIn(Math.toIntExact(jwtTokenValidity))
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
                .roles(new HashSet<>(request.getRoles() != null ? request.getRoles() : Set.of(AppUserRole.USER)))
                .isActive(false)
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
                .expiresIn(Math.toIntExact(jwtTokenValidity))
                .user(userInfo)
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}