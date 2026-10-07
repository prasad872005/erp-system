package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.PurchaseOrderRequest;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.entity.PurchaseOrder;
import com.erp.erp_backend.entity.PurchaseOrderStatus;
import com.erp.erp_backend.entity.Supplier;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.ProductRepository;
import com.erp.erp_backend.repository.PurchaseOrderRepository;
import com.erp.erp_backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public PurchaseOrderService(
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository) {

        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    public PurchaseOrder createPurchaseOrder(
            PurchaseOrderRequest request) {

        Supplier supplier = supplierRepository
                .findById(request.getSupplierId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found"
                        ));

        Product product = productRepository
                .findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found"
                        ));

        BigDecimal totalAmount = product.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        PurchaseOrder purchaseOrder = PurchaseOrder.builder()
                .supplier(supplier)
                .product(product)
                .quantity(request.getQuantity())
                .orderDate(LocalDate.now())
                .status(PurchaseOrderStatus.PENDING)
                .totalAmount(totalAmount)
                .build();

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder getPurchaseOrderById(Long id) {

        return purchaseOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Purchase order not found"
                        ));
    }

    public List<PurchaseOrder> getPurchaseOrdersBySupplier(
            Long supplierId) {

        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException(
                    "Supplier not found"
            );
        }

        return purchaseOrderRepository.findBySupplierId(supplierId);
    }

    public PurchaseOrder updatePurchaseOrderStatus(
            Long id,
            PurchaseOrderStatus status) {

        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);

        purchaseOrder.setStatus(status);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    public void deletePurchaseOrder(Long id) {

        PurchaseOrder purchaseOrder = getPurchaseOrderById(id);

        purchaseOrderRepository.delete(purchaseOrder);
    }
}