package com.example.productscrud.model.dto.response;

import com.example.productscrud.model.enumeration.Currency;
import com.example.productscrud.model.enumeration.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private String id;
    private String code;
    private String name;
    private String description;
    private BigDecimal price;
    private Currency currency;
    private ProductStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
