package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.GoodsReceivedNoteRequest;
import com.erp.erp_backend.entity.GoodsReceivedNote;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.entity.PurchaseOrder;
import com.erp.erp_backend.entity.PurchaseOrderStatus;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.GoodsReceivedNoteRepository;
import com.erp.erp_backend.repository.ProductRepository;
import com.erp.erp_backend.repository.PurchaseOrderRepository;
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
class GoodsReceivedNoteServiceTest {

    @Mock
    private GoodsReceivedNoteRepository goodsReceivedNoteRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GoodsReceivedNoteService goodsReceivedNoteService;

    private GoodsReceivedNoteRequest request;
    private Product product;
    private PurchaseOrder purchaseOrder;

    @BeforeEach
    void setUp() {

        request = new GoodsReceivedNoteRequest();
        request.setPurchaseOrderId(1L);
        request.setReceivedQuantity(5);

        product = Product.builder()
                .id(1L)
                .productName("Dell Laptop")
                .sku("DELL-001")
                .category("Electronics")
                .unitPrice(new BigDecimal("50000"))
                .currentStock(10)
                .reorderLevel(5)
                .build();

        purchaseOrder = PurchaseOrder.builder()
                .id(1L)
                .product(product)
                .quantity(10)
                .status(PurchaseOrderStatus.PENDING)
                .build();
    }

    @Test
    void createGoodsReceivedNote_shouldUpdateStockSuccessfully() {

        when(purchaseOrderRepository.findById(1L))
                .thenReturn(Optional.of(purchaseOrder));

        when(goodsReceivedNoteRepository
                .findByPurchaseOrderId(1L))
                .thenReturn(List.of());

        GoodsReceivedNote savedGrn = GoodsReceivedNote.builder()
                .id(1L)
                .purchaseOrder(purchaseOrder)
                .product(product)
                .receivedQuantity(5)
                .build();

        when(goodsReceivedNoteRepository.save(
                any(GoodsReceivedNote.class)))
                .thenReturn(savedGrn);

        GoodsReceivedNote result =
                goodsReceivedNoteService
                        .createGoodsReceivedNote(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getReceivedQuantity());

        assertEquals(15, product.getCurrentStock());

        verify(productRepository).save(product);
        verify(goodsReceivedNoteRepository)
                .save(any(GoodsReceivedNote.class));
        verify(purchaseOrderRepository)
                .save(purchaseOrder);
    }

    @Test
    void createGoodsReceivedNote_shouldThrowWhenPurchaseOrderNotFound() {

        when(purchaseOrderRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> goodsReceivedNoteService
                        .createGoodsReceivedNote(request)
        );

        verify(purchaseOrderRepository).findById(1L);

        verify(goodsReceivedNoteRepository, never())
                .save(any(GoodsReceivedNote.class));

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void createGoodsReceivedNote_shouldThrowWhenOrderIsCancelled() {

        purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);

        when(purchaseOrderRepository.findById(1L))
                .thenReturn(Optional.of(purchaseOrder));

        assertThrows(
                ResourceConflictException.class,
                () -> goodsReceivedNoteService
                        .createGoodsReceivedNote(request)
        );

        verify(goodsReceivedNoteRepository, never())
                .save(any(GoodsReceivedNote.class));

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void createGoodsReceivedNote_shouldThrowWhenQuantityExceedsRemaining() {

        request.setReceivedQuantity(11);

        when(purchaseOrderRepository.findById(1L))
                .thenReturn(Optional.of(purchaseOrder));

        when(goodsReceivedNoteRepository
                .findByPurchaseOrderId(1L))
                .thenReturn(List.of());

        assertThrows(
                ResourceConflictException.class,
                () -> goodsReceivedNoteService
                        .createGoodsReceivedNote(request)
        );

        verify(goodsReceivedNoteRepository, never())
                .save(any(GoodsReceivedNote.class));

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void getGoodsReceivedNoteById_shouldReturnGrn() {

        GoodsReceivedNote grn = GoodsReceivedNote.builder()
                .id(1L)
                .purchaseOrder(purchaseOrder)
                .product(product)
                .receivedQuantity(5)
                .build();

        when(goodsReceivedNoteRepository.findById(1L))
                .thenReturn(Optional.of(grn));

        GoodsReceivedNote result =
                goodsReceivedNoteService
                        .getGoodsReceivedNoteById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(5, result.getReceivedQuantity());

        verify(goodsReceivedNoteRepository).findById(1L);
    }

    @Test
    void getGoodsReceivedNoteById_shouldThrowNotFound() {

        when(goodsReceivedNoteRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> goodsReceivedNoteService
                        .getGoodsReceivedNoteById(99L)
        );

        verify(goodsReceivedNoteRepository).findById(99L);
    }
}