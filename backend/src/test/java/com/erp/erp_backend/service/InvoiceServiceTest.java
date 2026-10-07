package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.InvoiceRequest;
import com.erp.erp_backend.entity.Customer;
import com.erp.erp_backend.entity.Invoice;
import com.erp.erp_backend.entity.InvoiceStatus;
import com.erp.erp_backend.entity.SalesOrder;
import com.erp.erp_backend.entity.OrderStatus;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.InvoiceRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @InjectMocks
    private InvoiceService invoiceService;

    private InvoiceRequest request;
    private Customer customer;
    private SalesOrder salesOrder;

    @BeforeEach
    void setUp() {

        request = new InvoiceRequest();
        request.setSalesOrderId(1L);

        customer = Customer.builder()
                .id(1L)
                .customerName("ABC Enterprises")
                .email("abc@example.com")
                .phone("9876543210")
                .address("Mumbai")
                .build();

        salesOrder = SalesOrder.builder()
                .id(1L)
                .customer(customer)
                .quantity(2)
                .totalAmount(new BigDecimal("100000"))
                .status(OrderStatus.CONFIRMED)
                .build();
    }

    @Test
    void createInvoice_shouldCreateSuccessfully() {

        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.of(salesOrder));

        when(invoiceRepository.existsBySalesOrderId(1L))
                .thenReturn(false);

        Invoice savedInvoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("INV-123456")
                .salesOrder(salesOrder)
                .customer(customer)
                .totalAmount(new BigDecimal("100000"))
                .status(InvoiceStatus.ISSUED)
                .build();

        when(invoiceRepository.save(any(Invoice.class)))
                .thenReturn(savedInvoice);

        Invoice result =
                invoiceService.createInvoice(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("INV-123456", result.getInvoiceNumber());
        assertEquals(
                new BigDecimal("100000"),
                result.getTotalAmount()
        );
        assertEquals(
                InvoiceStatus.ISSUED,
                result.getStatus()
        );

        verify(salesOrderRepository).findById(1L);
        verify(invoiceRepository).existsBySalesOrderId(1L);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void createInvoice_shouldThrowWhenSalesOrderNotFound() {

        when(salesOrderRepository.findById(99L))
                .thenReturn(Optional.empty());

        request.setSalesOrderId(99L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> invoiceService.createInvoice(request)
        );

        verify(salesOrderRepository).findById(99L);

        verify(invoiceRepository, never())
                .save(any(Invoice.class));
    }

    @Test
    void createInvoice_shouldThrowWhenInvoiceAlreadyExists() {

        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.of(salesOrder));

        when(invoiceRepository.existsBySalesOrderId(1L))
                .thenReturn(true);

        assertThrows(
                ResourceConflictException.class,
                () -> invoiceService.createInvoice(request)
        );

        verify(invoiceRepository)
                .existsBySalesOrderId(1L);

        verify(invoiceRepository, never())
                .save(any(Invoice.class));
    }

    @Test
    void getInvoiceById_shouldReturnInvoice() {

        Invoice invoice = Invoice.builder()
                .id(1L)
                .invoiceNumber("INV-123456")
                .salesOrder(salesOrder)
                .customer(customer)
                .totalAmount(new BigDecimal("100000"))
                .status(InvoiceStatus.ISSUED)
                .build();

        when(invoiceRepository.findById(1L))
                .thenReturn(Optional.of(invoice));

        Invoice result =
                invoiceService.getInvoiceById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(
                "INV-123456",
                result.getInvoiceNumber()
        );

        verify(invoiceRepository).findById(1L);
    }

    @Test
    void getInvoiceById_shouldThrowNotFound() {

        when(invoiceRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> invoiceService.getInvoiceById(99L)
        );

        verify(invoiceRepository).findById(99L);
    }
}