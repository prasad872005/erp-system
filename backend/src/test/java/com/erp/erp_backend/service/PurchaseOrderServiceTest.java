package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.PurchaseOrderRequest;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.entity.PurchaseOrder;
import com.erp.erp_backend.entity.PurchaseOrderStatus;
import com.erp.erp_backend.entity.Supplier;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.ProductRepository;
import com.erp.erp_backend.repository.PurchaseOrderRepository;
import com.erp.erp_backend.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PurchaseOrderService purchaseOrderService;

    private PurchaseOrderRequest request;
    private Supplier supplier;
    private Product product;

    @BeforeEach
    void setUp() {

        request = new PurchaseOrderRequest();
        request.setSupplierId(1L);
        request.setProductId(1L);
        request.setQuantity(5);

        supplier = Supplier.builder()
                .id(1L)
                .supplierName("ABC Suppliers")
                .email("supplier@example.com")
                .phone("9876543210")
                .address("Mumbai")
                .build();

        product = Product.builder()
                .id(1L)
                .productName("Dell Laptop")
                .sku("DELL-001")
                .category("Electronics")
                .unitPrice(new BigDecimal("50000"))
                .currentStock(10)
                .reorderLevel(5)
                .build();
    }

    @Test
    void createPurchaseOrder_shouldCreateSuccessfully() {

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        PurchaseOrder savedOrder = PurchaseOrder.builder()
                .id(1L)
                .supplier(supplier)
                .product(product)
                .quantity(5)
                .totalAmount(new BigDecimal("250000"))
                .status(PurchaseOrderStatus.PENDING)
                .build();

        when(purchaseOrderRepository.save(any(PurchaseOrder.class)))
                .thenReturn(savedOrder);

        PurchaseOrder result =
                purchaseOrderService.createPurchaseOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getQuantity());
        assertEquals(
                new BigDecimal("250000"),
                result.getTotalAmount()
        );
        assertEquals(
                PurchaseOrderStatus.PENDING,
                result.getStatus()
        );

        verify(supplierRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(purchaseOrderRepository)
                .save(any(PurchaseOrder.class));
    }

    @Test
    void createPurchaseOrder_shouldThrowWhenSupplierNotFound() {

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> purchaseOrderService
                        .createPurchaseOrder(request)
        );

        verify(supplierRepository).findById(1L);

        verify(productRepository, never())
                .findById(anyLong());

        verify(purchaseOrderRepository, never())
                .save(any(PurchaseOrder.class));
    }

    @Test
    void createPurchaseOrder_shouldThrowWhenProductNotFound() {

        when(supplierRepository.findById(1L))
                .thenReturn(Optional.of(supplier));

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> purchaseOrderService
                        .createPurchaseOrder(request)
        );

        verify(supplierRepository).findById(1L);
        verify(productRepository).findById(1L);

        verify(purchaseOrderRepository, never())
                .save(any(PurchaseOrder.class));
    }

    @Test
    void getPurchaseOrderById_shouldReturnOrder() {

        PurchaseOrder order = PurchaseOrder.builder()
                .id(1L)
                .supplier(supplier)
                .product(product)
                .quantity(5)
                .totalAmount(new BigDecimal("250000"))
                .status(PurchaseOrderStatus.PENDING)
                .build();

        when(purchaseOrderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        PurchaseOrder result =
                purchaseOrderService.getPurchaseOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getQuantity());

        verify(purchaseOrderRepository).findById(1L);
    }

    @Test
    void getPurchaseOrderById_shouldThrowNotFound() {

        when(purchaseOrderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> purchaseOrderService
                        .getPurchaseOrderById(99L)
        );

        verify(purchaseOrderRepository).findById(99L);
    }
}