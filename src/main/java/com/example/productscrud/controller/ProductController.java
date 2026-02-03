package com.example.productscrud.controller;

import com.example.productscrud.model.dto.request.PartialProductUpdateRequest;
import com.example.productscrud.model.dto.request.ProductRequest;
import com.example.productscrud.model.enumeration.ProductStatus;
import com.example.productscrud.model.enumeration.code.ProductResponseCode;
import com.example.productscrud.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController("/api/v1/products")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ProductController extends BaseController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        return responseEntity(
                ProductResponseCode.PRODUCT_FETCHED,
                productService.getAllProducts(q, status, page, size, direction)
        );
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_WRITE','PRODUCT_READ')")
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request) {
        return responseEntity(ProductResponseCode.PRODUCT_CREATED, productService.create(request));
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN','PRODUCT_READ')")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        return responseEntity(ProductResponseCode.PRODUCT_FOUND, productService.getProductById(id));
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_WRITE')")
    public ResponseEntity<?> updateProductById(@Valid @RequestBody ProductRequest request, @PathVariable String id) {
        return responseEntity(ProductResponseCode.PRODUCT_UPDATED, productService.updateProductById(id,request));
    }
    @PatchMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_WRITE')")
    public ResponseEntity<?> partialUpdateProductById(@Valid @RequestBody PartialProductUpdateRequest request, @PathVariable String id) {
        return responseEntity(ProductResponseCode.PRODUCT_UPDATED, productService.partialUpdateProductById(id,request));
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_WRITE')")
    public ResponseEntity<?> deleteProductById( @PathVariable String id) {
        productService.deleteProductById(id);
        return responseEntity(ProductResponseCode.PRODUCT_DELETED,null);
    }
}
