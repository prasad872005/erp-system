package com.erp.erp_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = true,
            message = "Unit price cannot be negative")
    private BigDecimal unitPrice;

    @NotNull(message = "Current stock is required")
    @Min(value = 0, message = "Current stock cannot be negative")
    private Integer currentStock;

    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;
}