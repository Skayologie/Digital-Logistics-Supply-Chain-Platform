package com.project.supplychain.DTOs.productDTOs;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private UUID id;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    private boolean active;

    @NotNull(message = "Original price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Original price must be >= 0")
    private BigDecimal originalPrice;

    @NotNull(message = "Profit is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Profit must be >= 0")
    private BigDecimal profit;

}
