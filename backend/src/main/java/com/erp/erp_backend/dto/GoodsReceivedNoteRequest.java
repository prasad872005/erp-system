package com.erp.erp_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsReceivedNoteRequest {

    @NotNull(message = "Purchase Order ID is required")
    private Long purchaseOrderId;

    @NotNull(message = "Received quantity is required")
    @Min(value = 1, message = "Received quantity must be at least 1")
    private Integer receivedQuantity;
}