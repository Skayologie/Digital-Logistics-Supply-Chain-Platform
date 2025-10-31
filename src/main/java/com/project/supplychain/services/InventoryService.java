package com.project.supplychain.services;

import com.project.supplychain.DTOs.inventoryDTOs.InventoryDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.InventoryMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.Product;
import com.project.supplychain.models.Warehouse;
import com.project.supplychain.repositories.InventoryRepository;
import com.project.supplychain.repositories.ProductRepository;
import com.project.supplychain.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryMapper inventoryMapper;

    private void validateQuantities(InventoryDTO dto) {
        if (dto.getQtyOnHand() != null && dto.getQtyReserved() != null
                && dto.getQtyReserved() > dto.getQtyOnHand()) {
            throw new BadRequestException("qtyReserved cannot exceed qtyOnHand");
        }
    }

    public HashMap<String, Object> create(InventoryDTO dto) {
        validateQuantities(dto);

        Inventory entity = inventoryMapper.toEntity(dto);

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                .orElseThrow(() -> new BadRequestException("Warehouse not found"));

        entity.setProduct(product);
        entity.setWarehouse(warehouse);

        Inventory saved = inventoryRepository.save(entity);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Inventory created successfully");
        result.put("inventory", inventoryMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        Inventory found = inventoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Inventory not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("inventory", inventoryMapper.toDTO(found));
        return result;
    }

    public HashMap<String, Object> list() {
        List<InventoryDTO> list = inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("inventories", list);
        return result;
    }

    public HashMap<String, Object> update(UUID id, InventoryDTO dto) {
        validateQuantities(dto);
        Inventory existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Inventory not found"));

        existing.setQtyOnHand(dto.getQtyOnHand());
        existing.setQtyReserved(dto.getQtyReserved());

        if (dto.getProductId() != null) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found"));
            existing.setProduct(product);
        }

        if (dto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new BadRequestException("Warehouse not found"));
            existing.setWarehouse(warehouse);
        }

        Inventory saved = inventoryRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Inventory updated successfully");
        result.put("inventory", inventoryMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> delete(UUID id) {
        Inventory existing = inventoryRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Inventory not found"));
        inventoryRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Inventory deleted successfully");
        return result;
    }
}
