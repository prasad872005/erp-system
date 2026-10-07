package com.erp.erp_backend.controller;

import com.erp.erp_backend.dto.ProductRequest;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.service.ProductService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY_MANAGER')")
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY_MANAGER', 'SALES_EXECUTIVE')")
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY_MANAGER', 'SALES_EXECUTIVE')")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                productService.getProductById(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY_MANAGER')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {

        return ResponseEntity.ok(
                productService.updateProduct(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INVENTORY_MANAGER')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id
    ) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}