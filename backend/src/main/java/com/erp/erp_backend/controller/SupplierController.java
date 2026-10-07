package com.erp.erp_backend.controller;

import com.erp.erp_backend.dto.SupplierRequest;
import com.erp.erp_backend.entity.Supplier;
import com.erp.erp_backend.service.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'PURCHASE_MANAGER')")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    public ResponseEntity<Supplier> createSupplier(
            @Valid @RequestBody SupplierRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createSupplier(request));
    }

    @GetMapping
    public ResponseEntity<List<Supplier>> getAllSuppliers() {

        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Supplier> getSupplierById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                supplierService.getSupplierById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Supplier> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequest request) {

        return ResponseEntity.ok(
                supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(
            @PathVariable Long id) {

        supplierService.deleteSupplier(id);

        return ResponseEntity.noContent().build();
    }
}