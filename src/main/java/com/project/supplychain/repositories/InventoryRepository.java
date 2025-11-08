package com.project.supplychain.repositories;

import com.project.supplychain.models.Inventory;
import com.project.supplychain.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
	Optional<Inventory> findByProduct_IdAndWarehouse_Id(UUID productId, UUID warehouseId);

	List<Inventory> getAllByProduct_Active(boolean productActive);

	List<Inventory> getByQtyOnHandGreaterThanAndProduct(Integer qtyOnHandIsGreaterThan, Product product);
}
