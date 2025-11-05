package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO;
import com.project.supplychain.models.PurchaseOrder;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrderDTO toDTO(PurchaseOrder entity) {
        if (entity == null) return null;
        return PurchaseOrderDTO.builder()
                .id(entity.getId())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .expectedDelivery(entity.getExpectedDelivery())
                .supplierId(entity.getSupplier() != null ? entity.getSupplier().getId() : null)
                .warehouseManagerId(entity.getWarehouseManager() != null ? entity.getWarehouseManager().getId() : null)
                .build();
    }

    public PurchaseOrder toEntity(PurchaseOrderDTO dto) {
        if (dto == null) return null;
        PurchaseOrder entity = new PurchaseOrder();
        entity.setId(dto.getId());
        entity.setStatus(dto.getStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setExpectedDelivery(dto.getExpectedDelivery());
        return entity;
    }
}
