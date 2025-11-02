package com.project.supplychain.controller;

import com.project.supplychain.DTOs.supplierDTOs.SupplierDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.SupplierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SupplierDTO dto) {
        try {
            HashMap<String, Object> result = supplierService.createSupplier(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = supplierService.listSuppliers();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = supplierService.getSupplier(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody SupplierDTO dto) {
        try {
            HashMap<String, Object> result = supplierService.updateSupplier(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = supplierService.deleteSupplier(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
