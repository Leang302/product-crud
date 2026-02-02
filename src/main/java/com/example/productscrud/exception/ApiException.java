package com.example.productscrud.exception;

import com.example.productscrud.model.enumeration.code.ResponseCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private final ResponseCode code;

    public ApiException(ResponseCode code) {
        super(code.getDefaultMessage());
        this.code = code;
    }

    public ApiException(ResponseCode code, String customMessage) {
        super(customMessage != null ? customMessage : code.getDefaultMessage());
        this.code = code;
    }

    public String getCodeValue() {
        return code.getCode();
    }
}