package com.project.supplychain.controller;

import com.project.supplychain.DTOs.warehouseDTOs.WarehouseDTO;
import com.project.supplychain.annotations.RoleRequired;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/warehouses")
public class WarehouseController {

    @Autowired
    private WarehouseService warehouseService;

    @RoleRequired({"ADMIN","WAREHOUSE_MANAGER"})
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody WarehouseDTO dto) {
        try {
            HashMap<String, Object> result = warehouseService.create(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = warehouseService.list();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = warehouseService.get(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody WarehouseDTO dto) {
        try {
            HashMap<String, Object> result = warehouseService.update(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = warehouseService.delete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
