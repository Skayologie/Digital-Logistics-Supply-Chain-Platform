package com.project.supplychain.controller;

import com.project.supplychain.DTOs.shipmentDTOs.ShipmentDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/shipments")
public class ShipmentController {

    @Autowired
    private ShipmentService shipmentService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ShipmentDTO dto) {
        try {
            HashMap<String, Object> result = shipmentService.create(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = shipmentService.list();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = shipmentService.get(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody ShipmentDTO dto) {
        try {
            HashMap<String, Object> result = shipmentService.update(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/{id}/ship")
    public ResponseEntity<?> ship(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = shipmentService.ship(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/{id}/deliver")
    public ResponseEntity<?> deliver(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = shipmentService.deliver(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = shipmentService.delete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
