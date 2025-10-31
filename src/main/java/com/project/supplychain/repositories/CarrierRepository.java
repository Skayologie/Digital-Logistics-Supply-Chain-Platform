package com.project.supplychain.repositories;

import com.project.supplychain.models.Carrier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarrierRepository extends JpaRepository<Carrier, UUID> {
}
