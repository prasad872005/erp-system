package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.ProductRequest;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest request;

    @BeforeEach
    void setUp() {

        request = new ProductRequest();

        request.setProductName("Dell Laptop");
        request.setSku("DELL-001");
        request.setCategory("Electronics");
        request.setUnitPrice(new BigDecimal("55000"));
        request.setCurrentStock(10);
        request.setReorderLevel(5);
    }

    @Test
    void createProduct_shouldCreateProductSuccessfully() {

        when(productRepository.existsBySku("DELL-001"))
                .thenReturn(false);

        Product savedProduct = Product.builder()
                .id(1L)
                .productName("Dell Laptop")
                .sku("DELL-001")
                .category("Electronics")
                .unitPrice(new BigDecimal("55000"))
                .currentStock(10)
                .reorderLevel(5)
                .build();

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        Product result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Dell Laptop", result.getProductName());
        assertEquals("DELL-001", result.getSku());

        verify(productRepository).existsBySku("DELL-001");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_shouldThrowConflictWhenSkuExists() {

        when(productRepository.existsBySku("DELL-001"))
                .thenReturn(true);

        assertThrows(
                ResourceConflictException.class,
                () -> productService.createProduct(request)
        );

        verify(productRepository)
                .existsBySku("DELL-001");

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void getProductById_shouldReturnProduct() {

        Product product = Product.builder()
                .id(1L)
                .productName("Dell Laptop")
                .sku("DELL-001")
                .category("Electronics")
                .unitPrice(new BigDecimal("55000"))
                .currentStock(10)
                .reorderLevel(5)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Dell Laptop", result.getProductName());

        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_shouldThrowNotFoundWhenProductDoesNotExist() {

        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.getProductById(99L)
        );

        verify(productRepository).findById(99L);
    }

    @Test
    void getAllProducts_shouldReturnProducts() {

        Product product1 = Product.builder()
                .id(1L)
                .productName("Dell Laptop")
                .sku("DELL-001")
                .category("Electronics")
                .unitPrice(new BigDecimal("55000"))
                .currentStock(10)
                .reorderLevel(5)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("HP Mouse")
                .sku("HP-001")
                .category("Accessories")
                .unitPrice(new BigDecimal("1000"))
                .currentStock(20)
                .reorderLevel(5)
                .build();

        when(productRepository.findAll())
                .thenReturn(List.of(product1, product2));

        List<Product> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Dell Laptop", result.get(0).getProductName());
        assertEquals("HP Mouse", result.get(1).getProductName());

        verify(productRepository).findAll();
    }
}