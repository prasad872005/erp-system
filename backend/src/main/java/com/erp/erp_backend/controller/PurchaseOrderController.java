package com.erp.erp_backend.controller;

import com.erp.erp_backend.dto.PurchaseOrderRequest;
import com.erp.erp_backend.entity.PurchaseOrder;
import com.erp.erp_backend.entity.PurchaseOrderStatus;
import com.erp.erp_backend.service.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-orders")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(
            PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createPurchaseOrder(
            @Valid @RequestBody PurchaseOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(purchaseOrderService.createPurchaseOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrder>> getAllPurchaseOrders() {

        return ResponseEntity.ok(
                purchaseOrderService.getAllPurchaseOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                purchaseOrderService.getPurchaseOrderById(id));
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<PurchaseOrder>>
    getPurchaseOrdersBySupplier(
            @PathVariable Long supplierId) {

        return ResponseEntity.ok(
                purchaseOrderService
                        .getPurchaseOrdersBySupplier(supplierId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PurchaseOrder>
    updatePurchaseOrderStatus(
            @PathVariable Long id,
            @RequestParam PurchaseOrderStatus status) {

        return ResponseEntity.ok(
                purchaseOrderService.updatePurchaseOrderStatus(
                        id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchaseOrder(
            @PathVariable Long id) {

        purchaseOrderService.deletePurchaseOrder(id);

        return ResponseEntity.noContent().build();
    }
}