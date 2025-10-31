package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.inventoryDTOs.InventoryDTO;
import com.project.supplychain.models.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryDTO toDTO(Inventory entity) {
        if (entity == null) return null;
        return InventoryDTO.builder()
                .id(entity.getId())
                .qtyOnHand(entity.getQtyOnHand())
                .qtyReserved(entity.getQtyReserved())
                .warehouseId(entity.getWarehouse() != null ? entity.getWarehouse().getId() : null)
                .productId(entity.getProduct() != null ? entity.getProduct().getId() : null)
                .build();
    }

    public Inventory toEntity(InventoryDTO dto) {
        if (dto == null) return null;
        Inventory entity = new Inventory();
        entity.setId(dto.getId());
        entity.setQtyOnHand(dto.getQtyOnHand());
        entity.setQtyReserved(dto.getQtyReserved());
        // Relations are set in the service after fetching by IDs
        return entity;
    }
}
