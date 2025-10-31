package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.shipmentDTOs.ShipmentDTO;
import com.project.supplychain.models.Shipment;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {

    public ShipmentDTO toDTO(Shipment entity) {
        if (entity == null) return null;
        return ShipmentDTO.builder()
                .id(entity.getId())
                .trackingNumber(entity.getTrackingNumber())
                .status(entity.getStatus())
                .plannedDate(entity.getPlannedDate())
                .shippedDate(entity.getShippedDate())
                .deliveredDate(entity.getDeliveredDate())
                .salesOrderId(entity.getSalesOrder() != null ? entity.getSalesOrder().getId() : null)
                .carrierId(entity.getCarrier() != null ? entity.getCarrier().getId() : null)
                .build();
    }

    public Shipment toEntity(ShipmentDTO dto) {
        if (dto == null) return null;
        Shipment entity = new Shipment();
        entity.setId(dto.getId());
        entity.setTrackingNumber(dto.getTrackingNumber());
        entity.setStatus(dto.getStatus());
        entity.setPlannedDate(dto.getPlannedDate());
        entity.setShippedDate(dto.getShippedDate());
        entity.setDeliveredDate(dto.getDeliveredDate());
        return entity;
    }
}
