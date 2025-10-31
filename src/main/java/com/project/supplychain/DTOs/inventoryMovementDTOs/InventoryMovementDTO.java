package com.project.supplychain.DTOs.inventoryMovementDTOs;

import com.project.supplychain.enums.MovementType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementDTO {
    private UUID id;

    @NotNull(message = "type is required")
    private MovementType type;

    @NotNull(message = "quantity is required")
    private Integer quantity;

    private LocalDateTime occurredAt;
    private String referenceDocument;
    private String description;

    @NotNull(message = "inventoryId is required")
    private UUID inventoryId;
}
