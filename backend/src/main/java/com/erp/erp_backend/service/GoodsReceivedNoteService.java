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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class GoodsReceivedNoteService {

    private final GoodsReceivedNoteRepository goodsReceivedNoteRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ProductRepository productRepository;

    public GoodsReceivedNoteService(
            GoodsReceivedNoteRepository goodsReceivedNoteRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            ProductRepository productRepository) {

        this.goodsReceivedNoteRepository = goodsReceivedNoteRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public GoodsReceivedNote createGoodsReceivedNote(
            GoodsReceivedNoteRequest request) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository
                .findById(request.getPurchaseOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase order not found"
                        ));

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new ResourceConflictException(
                    "Cannot receive goods for a cancelled purchase order"
            );
        }

        int alreadyReceived = goodsReceivedNoteRepository
                .findByPurchaseOrderId(purchaseOrder.getId())
                .stream()
                .mapToInt(GoodsReceivedNote::getReceivedQuantity)
                .sum();

        int orderedQuantity = purchaseOrder.getQuantity();

        int remainingQuantity = orderedQuantity - alreadyReceived;

        if (request.getReceivedQuantity() > remainingQuantity) {
            throw new ResourceConflictException(
                    "Received quantity exceeds remaining purchase quantity"
            );
        }

        Product product = purchaseOrder.getProduct();

        product.setCurrentStock(
                product.getCurrentStock()
                        + request.getReceivedQuantity()
        );

        productRepository.save(product);

        GoodsReceivedNote grn = GoodsReceivedNote.builder()
                .purchaseOrder(purchaseOrder)
                .product(product)
                .receivedQuantity(request.getReceivedQuantity())
                .receivedDate(LocalDate.now())
                .build();

        GoodsReceivedNote savedGrn =
                goodsReceivedNoteRepository.save(grn);

        if (request.getReceivedQuantity() == remainingQuantity) {
            purchaseOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        } else {
            purchaseOrder.setStatus(PurchaseOrderStatus.ORDERED);
        }

        purchaseOrderRepository.save(purchaseOrder);

        return savedGrn;
    }

    public List<GoodsReceivedNote> getAllGoodsReceivedNotes() {
        return goodsReceivedNoteRepository.findAll();
    }

    public GoodsReceivedNote getGoodsReceivedNoteById(Long id) {

        return goodsReceivedNoteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "GRN not found"
                        ));
    }

    public List<GoodsReceivedNote> getByPurchaseOrder(
            Long purchaseOrderId) {

        if (!purchaseOrderRepository.existsById(purchaseOrderId)) {
            throw new ResourceNotFoundException(
                    "Purchase order not found"
            );
        }

        return goodsReceivedNoteRepository
                .findByPurchaseOrderId(purchaseOrderId);
    }

    public List<GoodsReceivedNote> getByProduct(Long productId) {

        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException(
                    "Product not found"
            );
        }

        return goodsReceivedNoteRepository
                .findByProductId(productId);
    }
}