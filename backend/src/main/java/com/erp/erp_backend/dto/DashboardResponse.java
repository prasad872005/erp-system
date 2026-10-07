package com.erp.erp_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class DashboardResponse {

    private long totalProducts;

    private long lowStockProducts;

    private long totalCustomers;

    private long totalSuppliers;

    private long totalSalesOrders;

    private long totalPurchaseOrders;

    private long totalInvoices;

    private BigDecimal totalSalesAmount;

    private BigDecimal totalPurchaseAmount;
}