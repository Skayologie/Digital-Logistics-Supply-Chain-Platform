package com.project.supplychain.models;

import com.project.supplychain.enums.CarrierStatus;
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
@Entity
public class Carrier {
    @Id
    @GeneratedValue
    private UUID id;
    private String code;
    private String name;
    private String phone;
    private String baseShippingRate;
    private Integer maxDailyCapacity;
    private Integer currentDailyShipments;
    private LocalDateTime cutOffTime;
    private CarrierStatus status;

    @OneToMany(mappedBy = "carrier", cascade = {CascadeType.ALL,CascadeType.MERGE}, orphanRemoval = true)
    private List<Shipment> shipments = new ArrayList<>();
}
