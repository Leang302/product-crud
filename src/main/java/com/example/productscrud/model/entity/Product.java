// 2. Product entity – improved naming & safety

package com.example.productscrud.model.entity;

import com.example.productscrud.model.dto.response.ProductResponse;
import com.example.productscrud.model.enumeration.Currency;
import com.example.productscrud.model.enumeration.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Product extends BaseEntityAudit {

    @Id
    @GeneratedValue
    private UUID internalId;
    @Column(nullable = false, unique = true, updatable = false)
    private String externalId;
    @Column(length = 50, nullable = false, unique = true)
    private String name;
    @Column( length = 50, nullable = false, unique = true)
    private String code;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductStatus status;


    public ProductResponse toResponse() {
        return ProductResponse.builder()
                .id(this.externalId)
                .code(this.code)
                .name(this.name)
                .description(this.description)
                .price(this.price)
                .currency(this.currency)
                .status(this.status)
                .createdAt(super.getCreatedAt())
                .updatedAt(super.getUpdatedAt())
                .build();
    }
}