package com.project.supplychain.services;

import com.project.supplychain.DTOs.salesOrderLineDTOs.SalesOrderLineDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.SalesOrderLineMapper;
import com.project.supplychain.models.*;
import com.project.supplychain.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SalesOrderLineService {

    @Autowired
    private SalesOrderLineRepository salesOrderLineRepository;
    @Autowired
    private SalesOrderRepository salesOrderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private InventoryRepository inventoryRepository;
    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;
    @Autowired
    private SalesOrderLineMapper mapper;

    private BigDecimal defaultUnitPrice(Product product) {
        BigDecimal base = product.getOriginalPrice() != null ? product.getOriginalPrice() : BigDecimal.ZERO;
        BigDecimal profit = product.getProfit() != null ? product.getProfit() : BigDecimal.ZERO;
        return base.add(profit);
    }

    private int safeInt(Integer v) { return v == null ? 0 : v; }

    private Inventory getInventoryOrThrow(Product product, SalesOrder order) {
        if (order.getWarehouse() == null) {
            throw new BadRequestException("Sales order has no warehouse assigned");
        }
        UUID productId = product.getId();
        UUID warehouseId = order.getWarehouse().getId();
        return inventoryRepository.findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseThrow(() -> new BadRequestException("No inventory found for product in order's warehouse"));
    }
    @Transactional
    public HashMap<String, Object> create(SalesOrderLineDTO dto) {
        HashMap<String, Object> result = new HashMap<>();

        SalesOrder order = salesOrderRepository.findById(dto.getSalesOrderId())
                .orElseThrow(() -> new BadRequestException("Sales order not found"));
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found"));

        List<Inventory> inventories = inventoryRepository
                .getByQtyOnHandGreaterThanAndProduct(0, product)
                .stream()
                .sorted(Comparator.comparingInt(inv -> safeInt(inv.getQtyOnHand()) - safeInt(inv.getQtyReserved())))
                .toList();

        if (inventories.isEmpty()) {
            throw new BadRequestException("No inventory available for this product");
        }

        int remainingQty = dto.getQuantity();

        SalesOrderLine entity = mapper.toEntity(dto);

        if (entity.getUnitPrice() == null) {
            entity.setUnitPrice(defaultUnitPrice(product));
        }

        entity.setProduct(product);
        entity.setSalesOrder(order);

        for (Inventory inventory : inventories) {
            int available = safeInt(inventory.getQtyOnHand()) - safeInt(inventory.getQtyReserved());

            if (available >= remainingQty) {
                inventory.setQtyReserved(inventory.getQtyReserved() + remainingQty);
                inventoryRepository.save(inventory);
                remainingQty = 0;
                break;
            }
        }

        if (remainingQty > 0) {
            for (Inventory inventory : inventories) {
                if (remainingQty <= 0) break;

                int available = safeInt(inventory.getQtyOnHand()) - safeInt(inventory.getQtyReserved());

                if (available > 0) {
                    int qtyToReserve = Math.min(available, remainingQty);
                    inventory.setQtyReserved(inventory.getQtyReserved() + qtyToReserve);
                    inventoryRepository.save(inventory);
                    remainingQty -= qtyToReserve;
                }
            }
        }

        boolean backorder = remainingQty > 0;
        entity.setBackorder(backorder);

        SalesOrderLine saved = salesOrderLineRepository.save(entity);

        result.put("message", backorder
                ? "Sales order line created (partial stock reserved, backorder for remaining)"
                : "Sales order line created and fully reserved");
        result.put("salesOrderLine", mapper.toDTO(saved));

        return result;
    }




    public Inventory getInventoryWithExactQuantity(Product product , Integer quantity){
        List<Inventory> inventories = inventoryRepository.getAllByProduct_Active(true);
        for(Inventory inventory : inventories){
            if(inventory.getProduct().equals(product)){
                if(inventory.getQtyOnHand() < quantity){
                    return inventory;
                }
            }
        }
        return null;
    }
    public List<Inventory> getAllInventoriesWithTheProduct(Product product){
        return inventoryRepository.getByQtyOnHandGreaterThanAndProduct(0,product)
                .stream()
                .sorted(Comparator.comparing(Inventory::getQtyOnHand))
                .toList();
    }

    public HashMap<String, Object> get(UUID id) {
        SalesOrderLine found = salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sales order line not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("salesOrderLine", mapper.toDTO(found));
        return result;
    }

    public HashMap<String, Object> getSalesOrderLinesBySO(UUID salesOrderId) {
        List<SalesOrderLine> lines = salesOrderLineRepository.findBySalesOrderId(salesOrderId);

        if (lines.isEmpty()) {
            throw new BadRequestException("No sales order lines found for this SalesOrder ID");
        }

        List<SalesOrderLineDTO> dtos = lines.stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        HashMap<String, Object> result = new HashMap<>();
        result.put("salesOrderLines", dtos);

        return result;
    }

    public HashMap<String, Object> list() {
        List<SalesOrderLineDTO> list = salesOrderLineRepository.findAll()
                .stream().map(mapper::toDTO).toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("salesOrderLines", list);
        return result;
    }

    @Transactional
    public HashMap<String, Object> update(UUID id, SalesOrderLineDTO dto) {
        SalesOrderLine existing = salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sales order line not found"));
        SalesOrder order = existing.getSalesOrder();
        if (order == null) throw new BadRequestException("Sales order line has no parent order");

        Product newProduct = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new BadRequestException("Product not found"));

        int oldQty = safeInt(existing.getQuantity());
        int newQty = dto.getQuantity();
        boolean wasReserved = !existing.isBackorder();

        Inventory oldInv = getInventoryOrThrow(existing.getProduct(), order);
        if (wasReserved && oldQty > 0) {
            oldInv.setQtyReserved(Math.max(0, safeInt(oldInv.getQtyReserved()) - oldQty));
            inventoryRepository.save(oldInv);
        }

        existing.setQuantity(newQty);
        existing.setUnitPrice(dto.getUnitPrice() != null ? dto.getUnitPrice() : defaultUnitPrice(newProduct));
        existing.setProduct(newProduct);

        Inventory newInv = getInventoryOrThrow(newProduct, order);
        int onHand = safeInt(newInv.getQtyOnHand());
        int reserved = safeInt(newInv.getQtyReserved());
        int available = onHand - reserved;
        boolean canReserve = newQty <= available;
        existing.setBackorder(!canReserve);
        if (canReserve && newQty > 0) {
            newInv.setQtyReserved(reserved + newQty);
            inventoryRepository.save(newInv);
        }

        SalesOrderLine saved = salesOrderLineRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", canReserve ? "Sales order line updated and reserved" : "Sales order line updated as backorder");
        result.put("salesOrderLine", mapper.toDTO(saved));
        return result;
    }

    @Transactional
    public HashMap<String, Object> delete(UUID id) {
        SalesOrderLine existing = salesOrderLineRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Sales order line not found"));
        SalesOrder order = existing.getSalesOrder();
        if (order == null) throw new BadRequestException("Sales order line has no parent order");
        if (!existing.isBackorder()) {
            Inventory inv = getInventoryOrThrow(existing.getProduct(), order);
            int newReserved = Math.max(0, safeInt(inv.getQtyReserved()) - safeInt(existing.getQuantity()));
            inv.setQtyReserved(newReserved);
            inventoryRepository.save(inv);
        }
        salesOrderLineRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Sales order line deleted and reservation adjusted");
        return result;
    }
}
