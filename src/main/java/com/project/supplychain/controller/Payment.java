package com.project.supplychain.controller;

import com.project.supplychain.DTOs.paymentDTOs.PaymentDto;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RequestMapping("api/paiment")
@RestController
public class Payment {

    @Autowired
    PaymentService paymentService;

    @PostMapping
    public HashMap<String , Object> makePayment(@Valid @RequestBody PaymentDto paymentDto){
        try{
            return paymentService.makePayment(paymentDto);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
