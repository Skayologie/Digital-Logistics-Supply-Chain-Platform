package com.project.supplychain.services;

import com.project.supplychain.DTOs.supplierDTOs.SupplierDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.SupplierMapper;
import com.project.supplychain.models.Supplier;
import com.project.supplychain.repositories.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    public HashMap<String, Object> createSupplier(SupplierDTO dto) {
        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setId(null);
        Supplier saved = supplierRepository.save(supplier);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Supplier created successfully");
        result.put("supplier", supplierMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> getSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Supplier not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("supplier", supplierMapper.toDTO(supplier));
        return result;
    }

    public HashMap<String, Object> listSuppliers() {
        List<SupplierDTO> suppliers = supplierRepository.findAll()
                .stream()
                .map(supplierMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("suppliers", suppliers);
        return result;
    }

    public HashMap<String, Object> updateSupplier(UUID id, SupplierDTO dto) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Supplier not found"));
        existing.setName(dto.getName());
        existing.setContactInfo(dto.getContactInfo());
        Supplier saved = supplierRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Supplier updated successfully");
        result.put("supplier", supplierMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> deleteSupplier(UUID id) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Supplier not found"));
        supplierRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Supplier deleted successfully");
        return result;
    }
}
