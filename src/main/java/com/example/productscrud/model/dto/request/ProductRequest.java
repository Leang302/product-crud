package com.example.productscrud.model.dto.request;

import com.example.productscrud.model.entity.Product;
import com.example.productscrud.model.enumeration.Currency;
import com.example.productscrud.model.enumeration.ProductStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Product code is required")
        @Size(min = 3, max = 30)
        String code,
        @Size(min = 1, max = 50)
        String name,
        @NotBlank
        @Size(min = 5, max = 500)
        String description,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 10, fraction = 2)
        BigDecimal price,

        @NotNull
        Currency currency,

        @NotNull
        ProductStatus status

) {
    public Product toEntity() {
        return Product.builder()
                .code(code)
                .name(name)
                .description(description)
                .price(price)
                .currency(currency)
                .status(status)
                .build();
    }
}