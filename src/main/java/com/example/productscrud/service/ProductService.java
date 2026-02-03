package com.example.productscrud.service;

import com.example.productscrud.model.dto.request.PartialProductUpdateRequest;
import com.example.productscrud.model.dto.request.ProductRequest;
import com.example.productscrud.model.dto.response.PaginationResponse;
import com.example.productscrud.model.dto.response.ProductResponse;
import com.example.productscrud.model.entity.Product;
import com.example.productscrud.model.enumeration.ProductStatus;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);

    ProductResponse getProductById(String id);

    Product findProductByIdInternal(String id);

    ProductResponse updateProductById(String id, ProductRequest request);

    ProductResponse partialUpdateProductById(String id, PartialProductUpdateRequest request);

    void deleteProductById(String id);

    PaginationResponse<List<ProductResponse>> getAllProducts(
            String q,
            ProductStatus status,
            int page,
            int size,
            Sort.Direction direction
    );
}
