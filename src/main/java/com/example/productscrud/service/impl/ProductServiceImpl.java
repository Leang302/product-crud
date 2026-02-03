package com.example.productscrud.service.impl;

import com.example.productscrud.exception.ApiException;
import com.example.productscrud.model.dto.request.PartialProductUpdateRequest;
import com.example.productscrud.model.dto.request.ProductRequest;
import com.example.productscrud.model.dto.response.PaginationResponse;
import com.example.productscrud.model.dto.response.ProductResponse;
import com.example.productscrud.model.entity.Product;
import com.example.productscrud.model.enumeration.ProductStatus;
import com.example.productscrud.model.enumeration.code.ProductResponseCode;
import com.example.productscrud.repository.ProductRepository;
import com.example.productscrud.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final String PREFIX = "prd_";
    private static final int PAD = 4;
    private final ProductRepository productRepository;

    @Override
    public ProductResponse create(ProductRequest request) {
        if (productRepository.existsByCode(request.code())) {
            throw new ApiException(ProductResponseCode.PRODUCT_ALREADY_EXISTS);
        }
        Long seq = productRepository.nextProductSequence();
        String productId = generateId(seq);

        Product product = request.toEntity();
        product.setExternalId(productId);
        return productRepository.save(product).toResponse();
    }

    @Override
    public ProductResponse getProductById(String id) {
        return findProductByIdInternal(id).toResponse();
    }

    @Override
    public Product findProductByIdInternal(String id) {
        return productRepository.findByExternalId(id).orElseThrow(() -> new ApiException(ProductResponseCode.PRODUCT_NOT_FOUND));
    }

    @Override
    public ProductResponse updateProductById(String id, ProductRequest request) {
        Product productById = findProductByIdInternal(id);
        if (productRepository.existsByCodeAndExternalIdNot(request.code(), id)) {
            throw new ApiException(ProductResponseCode.PRODUCT_ALREADY_EXISTS);
        }
        productById.setCode(request.code());
        productById.setDescription(request.description());
        productById.setPrice(request.price());
        productById.setCurrency(request.currency());
        productById.setStatus(request.status());
        return productRepository.save(productById).toResponse();
    }

    public String generateId(Long seq) {
        return PREFIX + String.format("%0" + PAD + "d", seq);
    }

    @Override
    public ProductResponse partialUpdateProductById(String id, PartialProductUpdateRequest request) {
        Product productById = findProductByIdInternal(id);
        productById.setPrice(request.price());
        productById.setStatus(request.status());
        return productRepository.save(productById).toResponse();
    }

    @Override
    public void deleteProductById(String id) {
        Product productById = findProductByIdInternal(id);
        if (productById.getStatus().equals(ProductStatus.ACTIVE)) {
            throw new ApiException(ProductResponseCode.PRODUCT_HAS_ACTIVE_ORDER);
        }
        productRepository.delete(productById);
    }

    @Override
    public PaginationResponse<List<ProductResponse>> getAllProducts(
            String q,
            ProductStatus status,
            int page,
            int size,
            Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "createdAt"));
        Page<Product> productPage;

        // Logic for filtering
        if (q != null && status != null) {
            productPage = productRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseAndStatus(q, q, status, pageable);
        } else if (q != null) {
            productPage = productRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(q, q, pageable);
        } else if (status != null) {
            productPage = productRepository.findByStatus(status, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        // Mapping content to DTOs
        List<ProductResponse> items = productPage.getContent()
                .stream()
                .map(Product::toResponse)
                .toList();

        // Wrapping into PaginationResponse
        return PaginationResponse.<List<ProductResponse>>builder()
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalItems((int) productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .items(items)
                .build();
    }

}

