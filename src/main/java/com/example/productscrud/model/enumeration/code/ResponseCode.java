package com.example.productscrud.model.enumeration.code;

import org.springframework.http.HttpStatus;

public interface ResponseCode {
    String getCode();
    String getDefaultMessage();
    HttpStatus getHttpStatus();
}