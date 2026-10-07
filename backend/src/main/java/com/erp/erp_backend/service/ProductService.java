package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.ProductRequest;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(ProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new ResourceConflictException(
                    "SKU already exists"
            );
        }

        Product product = Product.builder()
                .productName(request.getProductName())
                .sku(request.getSku())
                .category(request.getCategory())
                .unitPrice(request.getUnitPrice())
                .currentStock(request.getCurrentStock())
                .reorderLevel(request.getReorderLevel())
                .build();

        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found"
                        )
                );
    }

    public Product updateProduct(
            Long id,
            ProductRequest request
    ) {

        Product product = getProductById(id);

        if (!product.getSku().equals(request.getSku())
                && productRepository.existsBySku(request.getSku())) {

            throw new ResourceConflictException(
                    "SKU already exists"
            );
        }

        product.setProductName(request.getProductName());
        product.setSku(request.getSku());
        product.setCategory(request.getCategory());
        product.setUnitPrice(request.getUnitPrice());
        product.setCurrentStock(request.getCurrentStock());
        product.setReorderLevel(request.getReorderLevel());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {

        Product product = getProductById(id);

        productRepository.delete(product);
    }
}