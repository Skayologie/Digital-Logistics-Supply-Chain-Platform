package com.project.supplychain.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Inventory {
    @Id
    @GeneratedValue
    private UUID id;
    private Integer qtyOnHand;
    private Integer qtyReserved;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "inventory", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private List<InventoryMovement> inventoryMovements = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
