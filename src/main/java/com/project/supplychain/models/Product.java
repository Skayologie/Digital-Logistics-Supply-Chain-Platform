package com.project.supplychain.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Product {
    @Id
    @GeneratedValue
    private UUID id;
    private String sku;
    private String name;
    private String category;
    private boolean active;
    private BigDecimal originalPrice;
    private BigDecimal profit;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private List<SalesOrderLine> salesOrderLines = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private List<PurchaseOrderLine> purchaseOrderLines = new ArrayList<>();


}
