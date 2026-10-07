package com.erp.erp_backend.controller;

import com.erp.erp_backend.dto.InvoiceRequest;
import com.erp.erp_backend.entity.Invoice;
import com.erp.erp_backend.entity.InvoiceStatus;
import com.erp.erp_backend.service.InvoicePdfService;
import com.erp.erp_backend.service.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_EXECUTIVE', 'ACCOUNTANT')")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final InvoicePdfService invoicePdfService;

    public InvoiceController(
            InvoiceService invoiceService,
            InvoicePdfService invoicePdfService) {

        this.invoiceService = invoiceService;
        this.invoicePdfService = invoicePdfService;
    }

    @PostMapping
    public ResponseEntity<Invoice> createInvoice(
            @Valid @RequestBody InvoiceRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(invoiceService.createInvoice(request));
    }

    @GetMapping
    public ResponseEntity<List<Invoice>> getAllInvoices() {

        return ResponseEntity.ok(
                invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceById(id));
    }

    @GetMapping("/number/{invoiceNumber}")
    public ResponseEntity<Invoice> getInvoiceByNumber(
            @PathVariable String invoiceNumber) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Invoice>> getInvoicesByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                invoiceService.getInvoicesByCustomer(customerId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Invoice> updateInvoiceStatus(
            @PathVariable Long id,
            @RequestParam InvoiceStatus status) {

        return ResponseEntity.ok(
                invoiceService.updateInvoiceStatus(id, status));
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadInvoicePdf(
            @PathVariable Long id) {

        Invoice invoice = invoiceService.getInvoiceById(id);

        byte[] pdf =
                invoicePdfService.generateInvoicePdf(invoice);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename="
                                + invoice.getInvoiceNumber()
                                + ".pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}