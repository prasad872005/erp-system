package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.SalesOrderRequest;
import com.erp.erp_backend.entity.Customer;
import com.erp.erp_backend.entity.OrderStatus;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.entity.SalesOrder;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.CustomerRepository;
import com.erp.erp_backend.repository.ProductRepository;
import com.erp.erp_backend.repository.SalesOrderRepository;
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
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SalesOrderService salesOrderService;

    private SalesOrderRequest request;
    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {

        request = new SalesOrderRequest();
        request.setCustomerId(1L);
        request.setProductId(1L);
        request.setQuantity(2);

        customer = Customer.builder()
                .id(1L)
                .customerName("ABC Enterprises")
                .email("abc@example.com")
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
    void createSalesOrder_shouldCreateSuccessfully() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        SalesOrder savedOrder = SalesOrder.builder()
                .id(1L)
                .customer(customer)
                .product(product)
                .quantity(2)
                .totalAmount(new BigDecimal("100000"))
                .status(OrderStatus.PENDING)
                .build();

        when(salesOrderRepository.save(any(SalesOrder.class)))
                .thenReturn(savedOrder);

        SalesOrder result =
                salesOrderService.createSalesOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2, result.getQuantity());
        assertEquals(
                new BigDecimal("100000"),
                result.getTotalAmount()
        );
        assertEquals(
                OrderStatus.PENDING,
                result.getStatus()
        );

        verify(customerRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(salesOrderRepository).save(any(SalesOrder.class));
    }

    @Test
    void createSalesOrder_shouldThrowWhenCustomerNotFound() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> salesOrderService.createSalesOrder(request)
        );

        verify(customerRepository).findById(1L);

        verify(productRepository, never())
                .findById(anyLong());

        verify(salesOrderRepository, never())
                .save(any(SalesOrder.class));
    }

    @Test
    void createSalesOrder_shouldThrowWhenProductNotFound() {

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> salesOrderService.createSalesOrder(request)
        );

        verify(customerRepository).findById(1L);
        verify(productRepository).findById(1L);

        verify(salesOrderRepository, never())
                .save(any(SalesOrder.class));
    }

    @Test
    void createSalesOrder_shouldThrowWhenStockIsInsufficient() {

        product.setCurrentStock(1);

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                ResourceConflictException.class,
                () -> salesOrderService.createSalesOrder(request)
        );

        verify(salesOrderRepository, never())
                .save(any(SalesOrder.class));
    }

    @Test
    void getSalesOrderById_shouldReturnOrder() {

        SalesOrder order = SalesOrder.builder()
                .id(1L)
                .customer(customer)
                .product(product)
                .quantity(2)
                .totalAmount(new BigDecimal("100000"))
                .status(OrderStatus.PENDING)
                .build();

        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        SalesOrder result =
                salesOrderService.getSalesOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2, result.getQuantity());

        verify(salesOrderRepository).findById(1L);
    }

    @Test
    void getSalesOrderById_shouldThrowNotFound() {

        when(salesOrderRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> salesOrderService.getSalesOrderById(99L)
        );

        verify(salesOrderRepository).findById(99L);
    }
}