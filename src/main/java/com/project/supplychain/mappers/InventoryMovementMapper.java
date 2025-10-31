package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.models.InventoryMovement;
import org.springframework.stereotype.Component;

@Component
public class InventoryMovementMapper {

    public InventoryMovementDTO toDTO(InventoryMovement entity) {
        if (entity == null) return null;
        return InventoryMovementDTO.builder()
                .id(entity.getId())
                .type(entity.getType())
                .quantity(entity.getQuantity())
                .occurredAt(entity.getOccurredAt())
                .referenceDocument(entity.getReferenceDocument())
                .description(entity.getDescription())
                .inventoryId(entity.getInventory() != null ? entity.getInventory().getId() : null)
                .build();
    }

    public InventoryMovement toEntity(InventoryMovementDTO dto) {
        if (dto == null) return null;
        InventoryMovement entity = new InventoryMovement();
        entity.setId(dto.getId());
        entity.setType(dto.getType());
        entity.setQuantity(dto.getQuantity());
        entity.setOccurredAt(dto.getOccurredAt());
        entity.setReferenceDocument(dto.getReferenceDocument());
        entity.setDescription(dto.getDescription());
        return entity;
    }
}
