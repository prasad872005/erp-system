package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.DashboardResponse;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.repository.CustomerRepository;
import com.erp.erp_backend.repository.InvoiceRepository;
import com.erp.erp_backend.repository.ProductRepository;
import com.erp.erp_backend.repository.PurchaseOrderRepository;
import com.erp.erp_backend.repository.SalesOrderRepository;
import com.erp.erp_backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final SalesOrderRepository salesOrderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardService(
            ProductRepository productRepository,
            CustomerRepository customerRepository,
            SupplierRepository supplierRepository,
            SalesOrderRepository salesOrderRepository,
            PurchaseOrderRepository purchaseOrderRepository,
            InvoiceRepository invoiceRepository) {

        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.supplierRepository = supplierRepository;
        this.salesOrderRepository = salesOrderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public DashboardResponse getDashboard() {

        long totalProducts = productRepository.count();

        long lowStockProducts = productRepository.findAll()
                .stream()
                .filter(product ->
                        product.getCurrentStock()
                                <= product.getReorderLevel())
                .count();

        long totalCustomers = customerRepository.count();

        long totalSuppliers = supplierRepository.count();

        long totalSalesOrders = salesOrderRepository.count();

        long totalPurchaseOrders = purchaseOrderRepository.count();

        long totalInvoices = invoiceRepository.count();

        BigDecimal totalSalesAmount =
                salesOrderRepository.findAll()
                        .stream()
                        .map(order -> order.getTotalAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPurchaseAmount =
                purchaseOrderRepository.findAll()
                        .stream()
                        .map(order -> order.getTotalAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(
                totalProducts,
                lowStockProducts,
                totalCustomers,
                totalSuppliers,
                totalSalesOrders,
                totalPurchaseOrders,
                totalInvoices,
                totalSalesAmount,
                totalPurchaseAmount
        );
    }
}