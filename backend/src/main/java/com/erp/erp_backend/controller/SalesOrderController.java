package com.erp.erp_backend.controller;

import com.erp.erp_backend.dto.SalesOrderRequest;
import com.erp.erp_backend.entity.OrderStatus;
import com.erp.erp_backend.entity.SalesOrder;
import com.erp.erp_backend.service.SalesOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/sales-orders")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_EXECUTIVE')")
public class SalesOrderController {

    private final SalesOrderService salesOrderService;

    public SalesOrderController(SalesOrderService salesOrderService) {
        this.salesOrderService = salesOrderService;
    }

    @PostMapping
    public ResponseEntity<SalesOrder> createSalesOrder(
            @Valid @RequestBody SalesOrderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(salesOrderService.createSalesOrder(request));
    }

    @GetMapping
    public ResponseEntity<List<SalesOrder>> getAllSalesOrders() {

        return ResponseEntity.ok(
                salesOrderService.getAllSalesOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrder> getSalesOrderById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                salesOrderService.getSalesOrderById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<SalesOrder>> getSalesOrdersByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                salesOrderService.getSalesOrdersByCustomer(customerId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SalesOrder> updateSalesOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                salesOrderService.updateSalesOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSalesOrder(
            @PathVariable Long id) {

        salesOrderService.deleteSalesOrder(id);

        return ResponseEntity.noContent().build();
    }
}