package com.project.supplychain.services;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.DTOs.purchaseOrderLineDTOs.PurchaseOrderLineDTO;
import com.project.supplychain.enums.MovementType;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.PurchaseOrderLineMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.Product;
import com.project.supplychain.models.PurchaseOrder;
import com.project.supplychain.models.PurchaseOrderLine;
import com.project.supplychain.repositories.InventoryRepository;
import com.project.supplychain.repositories.ProductRepository;
import com.project.supplychain.repositories.PurchaseOrderLineRepository;
import com.project.supplychain.repositories.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderLineService {

    @Autowired
    private PurchaseOrderLineRepository purchaseOrderLineRepository;

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseOrderLineMapper purchaseOrderLineMapper;


    @Autowired
    private InventoryRepository inventoryRepository;

    public HashMap<String, Object> create(PurchaseOrderLineDTO dto) {
        PurchaseOrder order = purchaseOrderRepository.findById(dto.getPurchaseOrderId())
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));
        Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
                .orElseThrow(() -> new BadRequestException("Inventory not found"));

        switch (order.getStatus()) {
            case CREATED, APPROVED -> {
                // allowed
            }
            case RECEIVED -> throw new BadRequestException("Cannot add lines to a received order");
            case CANCELED -> throw new BadRequestException("Cannot add lines to a canceled order");
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found"));

        PurchaseOrderLine line = purchaseOrderLineMapper.toEntity(dto);
        line.setId(null);
        line.setPurchaseOrder(order);
        line.setProduct(product);
        line.setInventory(inventory);
        if (line.getUnitPrice() == null) {
            line.setUnitPrice(product.getOriginalPrice());
        }

        PurchaseOrderLine saved = purchaseOrderLineRepository.save(line);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order line created successfully");
        result.put("purchaseOrderLine", purchaseOrderLineMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        PurchaseOrderLine line = purchaseOrderLineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order line not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("purchaseOrderLine", purchaseOrderLineMapper.toDTO(line));
        return result;
    }

    public HashMap<String, Object> list() {
        List<PurchaseOrderLineDTO> lines = purchaseOrderLineRepository.findAll()
                .stream()
                .map(purchaseOrderLineMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("purchaseOrderLines", lines);
        return result;
    }

    public HashMap<String, Object> update(UUID id, PurchaseOrderLineDTO dto) {
        PurchaseOrderLine existing = purchaseOrderLineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order line not found"));
        PurchaseOrder order = existing.getPurchaseOrder();
        switch (order.getStatus()) {
            case CREATED, APPROVED -> {
                // allowed
            }
            case RECEIVED -> throw new BadRequestException("Cannot update lines of a received order");
            case CANCELED -> throw new BadRequestException("Cannot update lines of a canceled order");
        }

        if (dto.getProductId() != null && (existing.getProduct() == null || !dto.getProductId().equals(existing.getProduct().getId()))) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found"));
            existing.setProduct(product);
            if (dto.getUnitPrice() == null) {
                existing.setUnitPrice(product.getOriginalPrice());
            }
        }

        if (dto.getUnitPrice() != null) existing.setUnitPrice(dto.getUnitPrice());
        if (dto.getQuantity() != null) existing.setQuantity(dto.getQuantity());

        PurchaseOrderLine saved = purchaseOrderLineRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order line updated successfully");
        result.put("purchaseOrderLine", purchaseOrderLineMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> delete(UUID id) {
        PurchaseOrderLine existing = purchaseOrderLineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order line not found"));
        PurchaseOrder order = existing.getPurchaseOrder();
        switch (order.getStatus()) {
            case CREATED, APPROVED -> {
                // allowed
            }
            case RECEIVED -> throw new BadRequestException("Cannot delete lines of a received order");
            case CANCELED -> throw new BadRequestException("Cannot delete lines of a canceled order");
        }
        purchaseOrderLineRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order line deleted successfully");
        return result;
    }
}
