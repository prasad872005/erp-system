package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.InvoiceRequest;
import com.erp.erp_backend.entity.Customer;
import com.erp.erp_backend.entity.Invoice;
import com.erp.erp_backend.entity.InvoiceStatus;
import com.erp.erp_backend.entity.SalesOrder;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.InvoiceRepository;
import com.erp.erp_backend.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SalesOrderRepository salesOrderRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            SalesOrderRepository salesOrderRepository) {

        this.invoiceRepository = invoiceRepository;
        this.salesOrderRepository = salesOrderRepository;
    }

    public Invoice createInvoice(InvoiceRequest request) {

        SalesOrder salesOrder = salesOrderRepository
                .findById(request.getSalesOrderId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sales order not found"
                        ));

        if (invoiceRepository.existsBySalesOrderId(
                salesOrder.getId())) {

            throw new ResourceConflictException(
                    "Invoice already exists for this sales order"
            );
        }

        Customer customer = salesOrder.getCustomer();

        String invoiceNumber = generateInvoiceNumber();

        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .salesOrder(salesOrder)
                .customer(customer)
                .invoiceDate(LocalDate.now())
                .totalAmount(salesOrder.getTotalAmount())
                .status(InvoiceStatus.ISSUED)
                .build();

        return invoiceRepository.save(invoice);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public Invoice getInvoiceById(Long id) {

        return invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invoice not found"
                        ));
    }

    public Invoice getInvoiceByNumber(String invoiceNumber) {

        return invoiceRepository.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invoice not found"
                        ));
    }

    public List<Invoice> getInvoicesByCustomer(Long customerId) {

        return invoiceRepository.findByCustomerId(customerId);
    }

    public Invoice updateInvoiceStatus(
            Long id,
            InvoiceStatus status) {

        Invoice invoice = getInvoiceById(id);

        invoice.setStatus(status);

        return invoiceRepository.save(invoice);
    }

    private String generateInvoiceNumber() {

        return "INV-" + System.currentTimeMillis();
    }
}