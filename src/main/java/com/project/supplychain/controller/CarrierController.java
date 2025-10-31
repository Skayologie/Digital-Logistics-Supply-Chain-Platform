package com.project.supplychain.controller;

import com.project.supplychain.DTOs.carrierDTOs.CarrierDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.CarrierService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/carriers")
public class CarrierController {

    @Autowired
    private CarrierService carrierService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CarrierDTO dto) {
        try {
            HashMap<String, Object> result = carrierService.create(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = carrierService.list();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = carrierService.get(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody CarrierDTO dto) {
        try {
            HashMap<String, Object> result = carrierService.update(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = carrierService.activate(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/{id}/suspend")
    public ResponseEntity<?> suspend(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = carrierService.suspend(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PostMapping("/{id}/reset-daily-count")
    public ResponseEntity<?> resetDailyCount(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = carrierService.resetDailyCount(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = carrierService.delete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
