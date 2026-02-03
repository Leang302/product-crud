package com.example.productscrud.exception;

import com.example.productscrud.model.dto.response.ApiResponse;
import com.example.productscrud.model.dto.response.Status;
import com.example.productscrud.model.enumeration.code.ResponseCode;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalException {

    private static final Logger logger = LoggerFactory.getLogger(GlobalException.class);

    // 1. Custom business exceptions
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        ResponseCode code = ex.getCode();

        String message = ex.getMessage() != null ? ex.getMessage() : code.getDefaultMessage();

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .status(new Status(code.getCode(), message))
                .data(null)
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(body);
    }

    // 2. Bean validation failures (@Valid, @Validated parameters)
    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(Exception ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        if (ex instanceof MethodArgumentNotValidException mav) {
            for (FieldError fe : mav.getBindingResult().getFieldErrors()) {
                fieldErrors.put(fe.getField(), fe.getDefaultMessage());
            }
        } else if (ex instanceof HandlerMethodValidationException hmv) {
            hmv.getAllErrors().forEach(error -> {
                String paramName = "parameter";
                if (error instanceof org.springframework.validation.ObjectError oe) {
                    Object[] args = oe.getArguments();
                    if (args != null && args.length > 0) {
                        paramName = String.valueOf(args[0]);
                    }
                }
                fieldErrors.put(paramName, error.getDefaultMessage());
            });
        }

        String message = fieldErrors.isEmpty()
                ? "Validation failed"
                : fieldErrors.size() == 1
                ? fieldErrors.values().iterator().next()
                : "Validation failed for " + fieldErrors.size() + " fields";

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .status(new Status("VALIDATION_ERROR", message))
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 3. JSON parsing / deserialization errors (including invalid enum values)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String code = "INVALID_REQUEST_BODY";
        String message = "Request body is missing, malformed or contains invalid values. Please provide valid JSON.";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                String fieldPath = ife.getPath().isEmpty()
                        ? "unknown field"
                        : ife.getPath().get(0).getFieldName();

                String invalidValue = ife.getValue() != null
                        ? ife.getValue().toString()
                        : "null";

                // ── Fixed line ───────────────────────────────────────────────────────
                String allowed = Arrays.stream(ife.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                code = "INVALID_ENUM_VALUE";
                message = String.format(
                        "Invalid value '%s' for field '%s'. Allowed values: %s",
                        invalidValue, fieldPath, allowed
                );
            }
        }

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .status(new Status(code, message))
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 4. Wrong HTTP method
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String supported = ex.getSupportedHttpMethods() != null
                ? String.join(", ", ex.getSupportedHttpMethods().stream()
                .map(HttpMethod::name)
                .toList())
                : "unknown";

        String msg = String.format(
                "Method '%s' is not supported. Supported methods: %s",
                ex.getMethod(), supported
        );

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .status(new Status("METHOD_NOT_ALLOWED", msg))
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(body);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .status(new Status("UNAUTHENTICATED", "Authentication required. Please log in."))
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // ── NEW: Authorization / role failures (403) ──────────────────────────
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(Exception ex) {
        String username = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .orElse("anonymous");

        logger.warn("Access denied for user: {}", username, ex);

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .status(new Status("FORBIDDEN", "You do not have permission to perform this action."))
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }
    // 5. Catch-all + logging
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllOtherExceptions(Exception ex) {
        logger.error("Unhandled exception occurred", ex);

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .status(new Status(
                        "INTERNAL_SERVER_ERROR",
                        "An unexpected error occurred. Please contact support if the issue persists."
                ))
                .data(null)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}