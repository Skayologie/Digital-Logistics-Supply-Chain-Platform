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
public class Supplier {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String contactInfo;

    @OneToMany(mappedBy = "supplier", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();
}
