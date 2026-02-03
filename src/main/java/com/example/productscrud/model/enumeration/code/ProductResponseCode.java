package com.example.productscrud.model.enumeration.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductResponseCode implements ResponseCode {

    // ── Success responses ───────────────────────────────────────────────────────
    PRODUCT_CREATED("PRODUCT_CREATED", "Product created successfully", HttpStatus.CREATED),
    PRODUCT_UPDATED("PRODUCT_UPDATED", "Product updated successfully", HttpStatus.OK),
    PRODUCT_DELETED("SUCCESS", "Product deleted successfully", HttpStatus.OK),
    PRODUCT_FOUND("PRODUCT_FOUND", "Product retrieved successfully", HttpStatus.OK),
    PRODUCTS_LISTED("PRODUCTS_LISTED", "Products retrieved successfully", HttpStatus.OK),

    // ── Client errors ───────────────────────────────────────────────────────────
    PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_ALREADY_EXISTS("PRODUCT_ALREADY_EXISTS", "Code already exists", HttpStatus.CONFLICT),
    PRODUCT_HAS_ACTIVE_ORDER("PRODUCT_HAS_ACTIVE_ORDERS","Product cannot be deleted because active orders exists.", HttpStatus.CONFLICT),
    PRODUCT_FETCHED("PRODUCT_FETCHED","Product fetched." , HttpStatus.OK );
    private final String code;
    private final String defaultMessage;
    private final HttpStatus httpStatus;
}
