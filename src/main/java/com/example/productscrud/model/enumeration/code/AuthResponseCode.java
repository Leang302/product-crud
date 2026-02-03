package com.example.productscrud.model.enumeration.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthResponseCode implements ResponseCode {

    LOGIN_SUCCESS("AUTH_LOGIN_SUCCESS", "Login successful.", HttpStatus.OK),
    INVALID_CREDENTIALS("INVALID_CREDENTIALS", "Invalid username or password.", HttpStatus.UNAUTHORIZED),
    ACCOUNT_INACTIVE("AUTH_ACCOUNT_INACTIVE", "Account is inactive. Please contact administrator.", HttpStatus.FORBIDDEN),
    USERNAME_ALREADY_EXISTS("AUTH_USERNAME_ALREADY_EXISTS", "Username already taken.", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS("AUTH_EMAIL_ALREADY_EXISTS", "Email already registered.", HttpStatus.CONFLICT),
    USER_CREATED("USER_CREATED", "User created successfully.", HttpStatus.CREATED),

    BAD_REQUEST("BAD_REQUEST", "Invalid request data.", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED", "Account temporarily locked due to multiple failed login attempts", HttpStatus.FORBIDDEN);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}