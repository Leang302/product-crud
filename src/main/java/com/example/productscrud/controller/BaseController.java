package com.example.productscrud.controller;


import com.example.productscrud.model.response.ApiResponse;
import com.example.productscrud.model.response.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {
    public <T,K> ResponseEntity<ApiResponse<T,K>> responseEntity(String message, HttpStatus httpStatus,K code,
                                                             T data) {
        ApiResponse<T,K> response = ApiResponse.<T,K>builder()
                .data(data)
                .status(new Status<>(code,message))
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }

    public <T,K> ResponseEntity<ApiResponse<T,K>> responseEntity(String message, K code,
                                                                 T data) {
        return responseEntity(message, HttpStatus.OK,code, data);
    }

    public <T,K> ResponseEntity<ApiResponse<T,K>> responseEntity(String message,K code) {
        return responseEntity(message, HttpStatus.OK,code, null);
    }

    public <T,K> ResponseEntity<ApiResponse<T,K>> responseEntity(String message, HttpStatus httpStatus,K code) {
        return responseEntity(message, httpStatus,code, null);
    }
}
