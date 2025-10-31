package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.carrierDTOs.CarrierDTO;
import com.project.supplychain.models.Carrier;
import org.springframework.stereotype.Component;

@Component
public class CarrierMapper {

    public CarrierDTO toDTO(Carrier entity) {
        if (entity == null) return null;
        return CarrierDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .phone(entity.getPhone())
                .baseShippingRate(entity.getBaseShippingRate())
                .maxDailyCapacity(entity.getMaxDailyCapacity())
                .currentDailyShipments(entity.getCurrentDailyShipments())
                .cutOffTime(entity.getCutOffTime())
                .status(entity.getStatus())
                .build();
    }

    public Carrier toEntity(CarrierDTO dto) {
        if (dto == null) return null;
        Carrier entity = new Carrier();
        entity.setId(dto.getId());
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setPhone(dto.getPhone());
        entity.setBaseShippingRate(dto.getBaseShippingRate());
        entity.setMaxDailyCapacity(dto.getMaxDailyCapacity());
        entity.setCurrentDailyShipments(dto.getCurrentDailyShipments());
        entity.setCutOffTime(dto.getCutOffTime());
        entity.setStatus(dto.getStatus());
        return entity;
    }
}
