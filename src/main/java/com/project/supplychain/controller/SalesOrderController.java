package com.project.supplychain.controller;

import com.project.supplychain.DTOs.salesOrderDTOs.SalesOrderDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.SalesOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("api/salesOrders")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SalesOrderDTO dto) {
        try {
            HashMap<String, Object> result = salesOrderService.create(dto);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            HashMap<String, Object> result = salesOrderService.list();
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = salesOrderService.get(id);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody SalesOrderDTO dto) {
        try {
            HashMap<String, Object> result = salesOrderService.update(id, dto);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            HashMap<String, Object> result = salesOrderService.delete(id);
            return ResponseEntity.ok(result);
        } catch (AccessDeniedException ade) {
            throw ade;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
