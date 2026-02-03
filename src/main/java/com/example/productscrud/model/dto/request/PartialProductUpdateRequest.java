package com.example.productscrud.model.dto.request;

import com.example.productscrud.model.enumeration.ProductStatus;

import java.math.BigDecimal;

public record PartialProductUpdateRequest (
        BigDecimal price, ProductStatus status
){
}
