package com.erp.erp_backend.controller;

import com.erp.erp_backend.dto.GoodsReceivedNoteRequest;
import com.erp.erp_backend.entity.GoodsReceivedNote;
import com.erp.erp_backend.service.GoodsReceivedNoteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/grns")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER', 'INVENTORY_MANAGER')")
public class GoodsReceivedNoteController {

    private final GoodsReceivedNoteService goodsReceivedNoteService;

    public GoodsReceivedNoteController(
            GoodsReceivedNoteService goodsReceivedNoteService) {
        this.goodsReceivedNoteService = goodsReceivedNoteService;
    }

    @PostMapping
    public ResponseEntity<GoodsReceivedNote> createGoodsReceivedNote(
            @Valid @RequestBody GoodsReceivedNoteRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(goodsReceivedNoteService
                        .createGoodsReceivedNote(request));
    }

    @GetMapping
    public ResponseEntity<List<GoodsReceivedNote>>
    getAllGoodsReceivedNotes() {

        return ResponseEntity.ok(
                goodsReceivedNoteService
                        .getAllGoodsReceivedNotes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoodsReceivedNote>
    getGoodsReceivedNoteById(@PathVariable Long id) {

        return ResponseEntity.ok(
                goodsReceivedNoteService
                        .getGoodsReceivedNoteById(id));
    }

    @GetMapping("/purchase-order/{purchaseOrderId}")
    public ResponseEntity<List<GoodsReceivedNote>>
    getByPurchaseOrder(@PathVariable Long purchaseOrderId) {

        return ResponseEntity.ok(
                goodsReceivedNoteService
                        .getByPurchaseOrder(purchaseOrderId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<GoodsReceivedNote>>
    getByProduct(@PathVariable Long productId) {

        return ResponseEntity.ok(
                goodsReceivedNoteService
                        .getByProduct(productId));
    }
}