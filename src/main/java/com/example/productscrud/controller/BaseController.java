package com.example.productscrud.controller;

import com.example.productscrud.model.dto.response.ApiResponse;
import com.example.productscrud.model.dto.response.Status;
import com.example.productscrud.model.enumeration.code.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

    public <T> ResponseEntity<ApiResponse<T>> responseEntity(
            ResponseCode code,
            T data
    ) {
        return responseEntity(code, code.getDefaultMessage(), HttpStatus.OK, data);
    }

    public <T> ResponseEntity<ApiResponse<T>> responseEntity(
            ResponseCode code,
            String message,
            T data
    ) {
        return responseEntity(code, message, HttpStatus.OK, data);
    }

    public <T> ResponseEntity<ApiResponse<T>> responseEntity(
            ResponseCode code,
            String message,
            HttpStatus httpStatus,
            T data
    ) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .status(new Status(code.getCode(), message))
                .data(data)
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }

    public ResponseEntity<ApiResponse<Void>> responseEntity(
            ResponseCode code,
            HttpStatus httpStatus
    ) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(new Status(code.getCode(), code.getDefaultMessage()))
                .build();

        return ResponseEntity.status(httpStatus).body(response);
    }
}