package com.project.supplychain.services;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.enums.MovementType;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.InventoryMovementMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.InventoryMovement;
import com.project.supplychain.repositories.InventoryMovementRepository;
import com.project.supplychain.repositories.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryMovementService {

    @Autowired
    private InventoryMovementRepository movementRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryMovementMapper movementMapper;

    private void applyMovement(Inventory inv, MovementType type, int qty) {
        int current = inv.getQtyOnHand() != null ? inv.getQtyOnHand() : 0;
        int reserved = inv.getQtyReserved() != null ? inv.getQtyReserved() : 0;
        int newQty = current;

        switch (type) {
            case INBOUND -> {
                if (qty <= 0) throw new BadRequestException("INBOUND quantity must be > 0");
                newQty = current + qty;
            }
            case OUTBOUND -> {
                if (qty <= 0) throw new BadRequestException("OUTBOUND quantity must be > 0");
                int available = current - reserved;
                if (qty > available) {
                    throw new BadRequestException("Not enough available stock for OUTBOUND movement");
                }
                newQty = current - qty;
            }
            case ADJUSTMENT -> {
                newQty = current + qty;
            }
        }

        if (newQty < 0) throw new BadRequestException("Resulting qtyOnHand cannot be negative");
        if (newQty < reserved) throw new BadRequestException("Resulting qtyOnHand cannot be less than qtyReserved");

        inv.setQtyOnHand(newQty);
    }

    private void revertMovement(Inventory inv, MovementType type, int qty) {
        switch (type) {
            case INBOUND -> applyMovement(inv, MovementType.ADJUSTMENT, -qty);
            case OUTBOUND -> applyMovement(inv, MovementType.ADJUSTMENT, qty);
            case ADJUSTMENT -> applyMovement(inv, MovementType.ADJUSTMENT, -qty);
        }
    }

    @Transactional
    public HashMap<String, Object> create(InventoryMovementDTO dto) {
        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new BadRequestException("Inventory not found"));

        InventoryMovement entity = movementMapper.toEntity(dto);
        if (entity.getOccurredAt() == null) {
            entity.setOccurredAt(LocalDateTime.now());
        }

        applyMovement(inventory, entity.getType(), entity.getQuantity());

        entity.setInventory(inventory);
        movementRepository.save(entity);
        inventoryRepository.save(inventory);

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Inventory movement created successfully");
        result.put("inventoryMovement", movementMapper.toDTO(entity));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        InventoryMovement found = movementRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Inventory movement not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("inventoryMovement", movementMapper.toDTO(found));
        return result;
    }

    public HashMap<String, Object> list() {
        List<InventoryMovementDTO> list = movementRepository.findAll()
                .stream()
                .map(movementMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("inventoryMovements", list);
        return result;
    }

    @Transactional
    public HashMap<String, Object> delete(UUID id) {
        InventoryMovement existing = movementRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Inventory movement not found"));
        Inventory inv = existing.getInventory();
        if (inv == null) throw new BadRequestException("Inventory movement has no linked inventory");

        revertMovement(inv, existing.getType(), existing.getQuantity());

        movementRepository.delete(existing);
        inventoryRepository.save(inv);

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Inventory movement deleted and inventory adjusted");
        return result;
    }

    public HashMap<String, Object> update(UUID id, InventoryMovementDTO dto) {
        throw new BadRequestException("Updating inventory movements is not supported");
    }
}
