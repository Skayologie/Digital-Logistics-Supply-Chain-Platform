package com.project.supplychain.controller;

import com.project.supplychain.DTOs.inventoryMovementDTOs.InventoryMovementDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.InventoryMovementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/inventoryMovements")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementService movementService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody InventoryMovementDTO dto) {
        try {
            HashMap<String, Object> result = movementService.create(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = movementService.list();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = movementService.get(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = movementService.delete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
