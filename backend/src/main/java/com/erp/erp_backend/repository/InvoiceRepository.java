package com.erp.erp_backend.repository;

import com.erp.erp_backend.entity.Invoice;
import com.erp.erp_backend.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findBySalesOrderId(Long salesOrderId);

    List<Invoice> findByCustomerId(Long customerId);

    List<Invoice> findByStatus(InvoiceStatus status);

    boolean existsByInvoiceNumber(String invoiceNumber);

    boolean existsBySalesOrderId(Long salesOrderId);
}