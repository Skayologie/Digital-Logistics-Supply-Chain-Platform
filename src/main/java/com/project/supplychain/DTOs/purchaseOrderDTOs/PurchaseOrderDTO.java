package com.project.supplychain.DTOs.purchaseOrderDTOs;

import com.project.supplychain.DTOs.purchaseOrderLineDTOs.PurchaseOrderLineDTO;
import com.project.supplychain.enums.PurchaseOrderStatus;
import com.project.supplychain.models.PurchaseOrder;
import com.project.supplychain.models.PurchaseOrderLine;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDTO {
    private UUID id;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;

    @FutureOrPresent(message = "Expected delivery must be in the present or future")
    private LocalDateTime expectedDelivery;

    @NotNull(message = "Supplier id is required")
    private UUID supplierId;

    @NotNull(message = "Warehouse manager id is required")
    private UUID warehouseManagerId;


    private List<PurchaseOrderLine> purchaseOrderLines;
}
