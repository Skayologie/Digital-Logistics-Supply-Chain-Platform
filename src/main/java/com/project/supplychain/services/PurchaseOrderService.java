package com.project.supplychain.services;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.DTOs.purchaseOrderDTOs.PurchaseOrderDTO;
import com.project.supplychain.enums.PurchaseOrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.PurchaseOrderMapper;
import com.project.supplychain.mappers.PurchaseOrderLineMapper;
import com.project.supplychain.models.PurchaseOrder;
import com.project.supplychain.models.Supplier;
import com.project.supplychain.models.user.User;
import com.project.supplychain.models.user.WarehouseManager;
import com.project.supplychain.repositories.PurchaseOrderRepository;
import com.project.supplychain.repositories.SupplierRepository;
import com.project.supplychain.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseOrderMapper purchaseOrderMapper;

    @Autowired
    private PurchaseOrderLineMapper purchaseOrderLineMapper;

    @Autowired
    private InventoryMovementService inventoryMovementService;


    public HashMap<String, Object> create(PurchaseOrderDTO dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new BadRequestException("Supplier not found"));
        User user = userRepository.findById(dto.getWarehouseManagerId())
                .orElseThrow(() -> new BadRequestException("Warehouse manager not found"));
        if (!(user instanceof WarehouseManager wm)) {
            throw new BadRequestException("Provided user is not a WarehouseManager");
        }

        PurchaseOrder po = purchaseOrderMapper.toEntity(dto);
        po.setId(null);
        po.setSupplier(supplier);
        po.setWarehouseManager(wm);
        po.setStatus(PurchaseOrderStatus.CREATED);
        po.setCreatedAt(LocalDateTime.now());
        po.setExpectedDelivery(dto.getExpectedDelivery());

        PurchaseOrder saved = purchaseOrderRepository.save(po);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order created successfully");
        result.put("purchaseOrder", purchaseOrderMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));
        HashMap<String, Object> result = new HashMap<>();
    result.put("purchaseOrder", purchaseOrderMapper.toDTO(po));
    result.put("POLines", po.getPurchaseOrderLines()
        .stream()
        .map(purchaseOrderLineMapper::toDTO)
        .toList());
        return result;
    }

    public HashMap<String, Object> getOPLinesById(UUID id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));
        HashMap<String, Object> result = new HashMap<>();
    result.put("POLines", po.getPurchaseOrderLines()
        .stream()
        .map(purchaseOrderLineMapper::toDTO)
        .toList());
        return result;
    }

    public HashMap<String, Object> list() {
        List<HashMap<String, Object>> listWithLines = purchaseOrderRepository.findAll()
                .stream()
                .map(po -> {
                    HashMap<String, Object> item = new HashMap<>();
                    item.put("purchaseOrder", purchaseOrderMapper.toDTO(po));
                    item.put("POLines", po.getPurchaseOrderLines()
                            .stream()
                            .map(purchaseOrderLineMapper::toDTO)
                            .toList());
                    return item;
                })
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("purchaseOrders", listWithLines);
        return result;
    }

    public HashMap<String, Object> update(UUID id, PurchaseOrderDTO dto) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));

        if (existing.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new BadRequestException("Cannot update a received purchase order");
        }
        if (existing.getStatus() == PurchaseOrderStatus.CANCELED) {
            throw new BadRequestException("Cannot update a canceled purchase order");
        }

        if (dto.getSupplierId() != null && (existing.getSupplier() == null || !dto.getSupplierId().equals(existing.getSupplier().getId()))) {
            Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new BadRequestException("Supplier not found"));
            existing.setSupplier(supplier);
        }

        if (dto.getWarehouseManagerId() != null && (existing.getWarehouseManager() == null || !dto.getWarehouseManagerId().equals(existing.getWarehouseManager().getId()))) {
            User user = userRepository.findById(dto.getWarehouseManagerId())
                    .orElseThrow(() -> new BadRequestException("Warehouse manager not found"));
            if (!(user instanceof WarehouseManager wm)) {
                throw new BadRequestException("Provided user is not a WarehouseManager");
            }
            existing.setWarehouseManager(wm);
        }

        existing.setExpectedDelivery(dto.getExpectedDelivery());

        PurchaseOrder saved = purchaseOrderRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order updated successfully");
        result.put("purchaseOrder", purchaseOrderMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> delete(UUID id) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));
        if (existing.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new BadRequestException("Cannot delete a received purchase order");
        }
        purchaseOrderRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order deleted successfully");
        return result;
    }

    public HashMap<String, Object> approve(UUID id) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));
        if (existing.getStatus() != PurchaseOrderStatus.CREATED) {
            throw new BadRequestException("Only CREATED orders can be approved");
        }
        existing.setStatus(PurchaseOrderStatus.APPROVED);
        PurchaseOrder saved = purchaseOrderRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order approved");
        result.put("purchaseOrder", purchaseOrderMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> cancel(UUID id) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));
        if (existing.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new BadRequestException("Cannot cancel a received order");
        }
        existing.setStatus(PurchaseOrderStatus.CANCELED);
        PurchaseOrder saved = purchaseOrderRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order canceled");
        result.put("purchaseOrder", purchaseOrderMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> receive(UUID id) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Purchase order not found"));
        if (existing.getStatus() != PurchaseOrderStatus.APPROVED) {
            throw new BadRequestException("Only APPROVED orders can be received");
        }
        existing.setStatus(PurchaseOrderStatus.RECEIVED);
        PurchaseOrder saved = purchaseOrderRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Purchase order received");
        result.put("purchaseOrder", purchaseOrderMapper.toDTO(saved));
        return result;
    }
}
