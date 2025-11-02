package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.supplierDTOs.SupplierDTO;
import com.project.supplychain.models.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public SupplierDTO toDTO(Supplier entity) {
        if (entity == null) return null;
        return SupplierDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .contactInfo(entity.getContactInfo())
                .build();
    }

    public Supplier toEntity(SupplierDTO dto) {
        if (dto == null) return null;
        Supplier entity = new Supplier();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setContactInfo(dto.getContactInfo());
        return entity;
    }
}
