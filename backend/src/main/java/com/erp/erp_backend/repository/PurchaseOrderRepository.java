package com.erp.erp_backend.repository;

import com.erp.erp_backend.entity.PurchaseOrder;
import com.erp.erp_backend.entity.PurchaseOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderRepository
        extends JpaRepository<PurchaseOrder, Long> {

    List<PurchaseOrder> findBySupplierId(Long supplierId);

    List<PurchaseOrder> findByStatus(PurchaseOrderStatus status);
}