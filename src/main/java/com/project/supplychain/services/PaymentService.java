package com.project.supplychain.services;


import com.project.supplychain.DTOs.paymentDTOs.PaymentDto;
import com.project.supplychain.enums.OrderStatus;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.models.SalesOrder;
import com.project.supplychain.repositories.SalesOrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Transactional
    public HashMap<String, Object> makePayment(PaymentDto paymentDto) {
        HashMap<String, Object> response = new HashMap<>();

        SalesOrder salesOrder = salesOrderRepository.findById(UUID.fromString(paymentDto.getSalesOrderId()))
                .orElseThrow(() -> new BadRequestException("Sales order not found"));

        if (salesOrder.getStatus() != OrderStatus.RESERVED) {
            throw new BadRequestException("Payment cannot be processed: Order is not in RESERVED status");
        }

        salesOrder.setStatus(OrderStatus.SHIPPED);
        salesOrderRepository.save(salesOrder);

        response.put("message", "The order has been paid successfully");
        response.put("status", "SHIPPED");

        return response;
    }
}
