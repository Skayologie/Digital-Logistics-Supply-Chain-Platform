package com.project.supplychain.controller;

import com.project.supplychain.DTOs.salesOrderLineDTOs.SalesOrderLineDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.SalesOrderLineService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/salesOrderLines")
public class SalesOrderLineController {

    @Autowired
    private SalesOrderLineService salesOrderLineService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SalesOrderLineDTO dto) {
        try {
            HashMap<String, Object> result = salesOrderLineService.create(dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = salesOrderLineService.list();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = salesOrderLineService.get(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}/OS")
    public ResponseEntity<?> getbyOS(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = salesOrderLineService.getSalesOrderLinesBySO(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody SalesOrderLineDTO dto) {
        try {
            HashMap<String, Object> result = salesOrderLineService.update(id, dto);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = salesOrderLineService.delete(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
