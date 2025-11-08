package com.project.supplychain.services;

import com.project.supplychain.DTOs.salesOrderDTOs.SalesOrderDTO;
import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.SalesOrderMapper;
import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.Warehouse;
import com.project.supplychain.models.user.Client;
import com.project.supplychain.models.user.User;
import com.project.supplychain.repositories.InventoryRepository;
import com.project.supplychain.repositories.SalesOrderRepository;
import com.project.supplychain.repositories.UserRepository;
import com.project.supplychain.repositories.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    public HashMap<String, Object> create(SalesOrderDTO dto) {
        if (dto.getClientId() == null) {
            throw new BadRequestException("clientId is required");
        }

        SalesOrder entity = salesOrderMapper.toEntity(dto);

        User user = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new BadRequestException("Client not found"));
        if (!(user instanceof Client client)) {
            throw new BadRequestException("Provided user is not a client");
        }


        Warehouse warehouse = warehouseRepository.getById(dto.getWarehouseId());

        entity.setClient(client);
        entity.setStatus(OrderStatus.RESERVED);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setWarehouse(warehouse);

        SalesOrder saved = salesOrderRepository.save(entity);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Sales order created successfully");
        result.put("salesOrder", salesOrderMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        SalesOrder found = salesOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sales order not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("salesOrder", salesOrderMapper.toDTO(found));
        return result;
    }

    public HashMap<String, Object> list() {
        List<SalesOrderDTO> list = salesOrderRepository.findAll()
                .stream()
                .map(salesOrderMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("salesOrders", list);
        return result;
    }

    public HashMap<String, Object> update(UUID id, SalesOrderDTO dto) {
        SalesOrder existing = salesOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sales order not found"));

        existing.setStatus(dto.getStatus());
        existing.setCreatedAt(dto.getCreatedAt());
        existing.setReservedAt(dto.getReservedAt());
        existing.setShippedAt(dto.getShippedAt());
        existing.setDeliveredAt(dto.getDeliveredAt());

        SalesOrder saved = salesOrderRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Sales order updated successfully");
        result.put("salesOrder", salesOrderMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> delete(UUID id) {
        SalesOrder existing = salesOrderRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sales order not found"));
        salesOrderRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Sales order deleted successfully");
        return result;
    }
}
