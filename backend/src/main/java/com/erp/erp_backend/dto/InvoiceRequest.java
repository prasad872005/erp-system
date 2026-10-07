package com.erp.erp_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceRequest {

    @NotNull(message = "Sales Order ID is required")
    private Long salesOrderId;
}