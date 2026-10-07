package com.erp.erp_backend.repository;

import com.erp.erp_backend.entity.SalesOrder;
import com.erp.erp_backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    List<SalesOrder> findByCustomerId(Long customerId);

    List<SalesOrder> findByStatus(OrderStatus status);
}