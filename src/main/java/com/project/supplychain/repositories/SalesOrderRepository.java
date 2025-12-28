package com.project.supplychain.repositories;

import com.project.supplychain.models.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    SalesOrder getById(UUID id);

    List<SalesOrder> findByClientId(UUID clientId);
}
