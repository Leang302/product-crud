package com.example.productscrud.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Status<T> {
    private T code;
    private String message;
}
