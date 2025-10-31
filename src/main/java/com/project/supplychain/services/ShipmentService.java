package com.project.supplychain.services;

import com.project.supplychain.DTOs.shipmentDTOs.ShipmentDTO;
import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.enums.ShipmentStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.ShipmentMapper;
import com.project.supplychain.models.Carrier;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.models.Shipment;
import com.project.supplychain.repositories.CarrierRepository;
import com.project.supplychain.repositories.SalesOrderRepository;
import com.project.supplychain.repositories.ShipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentService {

    @Autowired
    private ShipmentRepository shipmentRepository;
    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private CarrierRepository carrierRepository;
    @Autowired
    private ShipmentMapper shipmentMapper;

    @Autowired
    private CarrierService carrierService;

    public HashMap<String, Object> create(ShipmentDTO dto) {
        Shipment entity = shipmentMapper.toEntity(dto);

        SalesOrder order = salesOrderRepository.findById(dto.getSalesOrderId())
                .orElseThrow(() -> new BadRequestException("Sales order not found"));
        Carrier carrier = carrierRepository.findById(dto.getCarrierId())
                .orElseThrow(() -> new BadRequestException("Carrier not found"));

        if (entity.getStatus() == null) entity.setStatus(ShipmentStatus.PLANNED);
        if (entity.getPlannedDate() == null) entity.setPlannedDate(LocalDateTime.now());

        entity.setSalesOrder(order);
        entity.setCarrier(carrier);

        Shipment saved = shipmentRepository.save(entity);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Shipment created successfully");
        result.put("shipment", shipmentMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> get(UUID id) {
        Shipment found = shipmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Shipment not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("shipment", shipmentMapper.toDTO(found));
        return result;
    }

    public HashMap<String, Object> list() {
        List<ShipmentDTO> list = shipmentRepository.findAll()
                .stream()
                .map(shipmentMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("shipments", list);
        return result;
    }

    public HashMap<String, Object> update(UUID id, ShipmentDTO dto) {
        Shipment existing = shipmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Shipment not found"));

        if (existing.getStatus() == ShipmentStatus.PLANNED) {
            existing.setTrackingNumber(dto.getTrackingNumber());
            existing.setPlannedDate(dto.getPlannedDate());
            if (dto.getCarrierId() != null) {
                Carrier carrier = carrierRepository.findById(dto.getCarrierId())
                        .orElseThrow(() -> new BadRequestException("Carrier not found"));
                existing.setCarrier(carrier);
            }
        } else {
            throw new BadRequestException("Only PLANNED shipments can be updated");
        }

        Shipment saved = shipmentRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Shipment updated successfully");
        result.put("shipment", shipmentMapper.toDTO(saved));
        return result;
    }

    @Transactional
    public HashMap<String, Object> ship(UUID id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Shipment not found"));
        if (shipment.getStatus() != ShipmentStatus.PLANNED) {
            throw new BadRequestException("Only PLANNED shipments can be shipped");
        }
        // Validate carrier constraints before shipping
        Carrier carrier = shipment.getCarrier();
        if (carrier == null) {
            throw new BadRequestException("Shipment has no carrier assigned");
        }
        carrierService.ensureCanShip(carrier);

        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setShippedDate(LocalDateTime.now());

        SalesOrder order = shipment.getSalesOrder();
        if (order != null) {
            order.setStatus(OrderStatus.SHIPPED);
            order.setShippedAt(LocalDateTime.now());
            salesOrderRepository.save(order);
        }

        Shipment saved = shipmentRepository.save(shipment);
        carrierService.incrementDailyShipments(carrier);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Shipment marked as IN_TRANSIT");
        result.put("shipment", shipmentMapper.toDTO(saved));
        return result;
    }

    @Transactional
    public HashMap<String, Object> deliver(UUID id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Shipment not found"));
        if (shipment.getStatus() != ShipmentStatus.IN_TRANSIT) {
            throw new BadRequestException("Only IN_TRANSIT shipments can be delivered");
        }
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredDate(LocalDateTime.now());

        SalesOrder order = shipment.getSalesOrder();
        if (order != null) {
            order.setStatus(OrderStatus.DELIVERED);
            order.setDeliveredAt(LocalDateTime.now());
            salesOrderRepository.save(order);
        }

        Shipment saved = shipmentRepository.save(shipment);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Shipment marked as DELIVERED");
        result.put("shipment", shipmentMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> delete(UUID id) {
        Shipment existing = shipmentRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Shipment not found"));
        if (existing.getStatus() == ShipmentStatus.IN_TRANSIT || existing.getStatus() == ShipmentStatus.DELIVERED) {
            throw new BadRequestException("Cannot delete a shipment that is already shipped or delivered");
        }
        shipmentRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Shipment deleted successfully");
        return result;
    }
}
