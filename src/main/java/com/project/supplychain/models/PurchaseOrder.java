package com.project.supplychain.models;

import com.project.supplychain.enums.PurchaseOrderStatus;
import com.project.supplychain.models.user.WarehouseManager;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Setter
@Getter
@Entity(name = "purchase_order")
public class PurchaseOrder {
    @Id
    @GeneratedValue
    private UUID id;
    private PurchaseOrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expectedDelivery;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "warehouse_manager_id")
    private WarehouseManager warehouseManager;

    @OneToMany(mappedBy = "purchaseOrder", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private List<PurchaseOrderLine> purchaseOrderLines;


}
