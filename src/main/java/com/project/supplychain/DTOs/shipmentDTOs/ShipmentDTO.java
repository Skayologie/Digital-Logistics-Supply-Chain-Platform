package com.project.supplychain.DTOs.shipmentDTOs;

import com.project.supplychain.enums.ShipmentStatus;
import jakarta.validation.constraints.NotBlank;
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
public class ShipmentDTO {
    private UUID id;

    @NotBlank(message = "trackingNumber is required")
    private String trackingNumber;

    private ShipmentStatus status;
    private LocalDateTime plannedDate;
    private LocalDateTime shippedDate;
    private LocalDateTime deliveredDate;

    @NotNull(message = "salesOrderId is required")
    private UUID salesOrderId;

    @NotNull(message = "carrierId is required")
    private UUID carrierId;
}
