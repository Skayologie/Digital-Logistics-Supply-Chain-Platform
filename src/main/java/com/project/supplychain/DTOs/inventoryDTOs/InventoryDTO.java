package com.project.supplychain.DTOs.inventoryDTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryDTO {
    private UUID id;

    @NotNull(message = "qtyOnHand is required")
    @Min(value = 0, message = "qtyOnHand must be >= 0")
    private Integer qtyOnHand;

    @NotNull(message = "qtyReserved is required")
    @Min(value = 0, message = "qtyReserved must be >= 0")
    private Integer qtyReserved;

    @NotNull(message = "warehouseId is required")
    private UUID warehouseId;

    @NotNull(message = "productId is required")
    private UUID productId;
}
