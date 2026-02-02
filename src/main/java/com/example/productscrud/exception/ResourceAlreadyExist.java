package com.example.productscrud.exception;

public class ResourceAlreadyExist extends RuntimeException {
    public ResourceAlreadyExist(String message) {
        super(message);
    }
}
