package com.example.productscrud.repository;

import com.example.productscrud.model.entity.Product;
import com.example.productscrud.model.enumeration.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsByCode(String code);

    boolean existsByCodeAndExternalIdNot(String code, String id);
    @Query(value = "SELECT nextval('product_seq')", nativeQuery = true)
    Long nextProductSequence();

    Optional<Product> findByExternalId(String id);

    // Search by Name OR Code AND Status
    Page<Product> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseAndStatus(
            String name, String code, ProductStatus status, Pageable pageable);

    // Search by Name OR Code
    Page<Product> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
            String name, String code, Pageable pageable);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);
}
