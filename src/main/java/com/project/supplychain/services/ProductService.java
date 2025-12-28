package com.project.supplychain.services;

import com.project.supplychain.DTOs.productDTOs.ProductDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.ProductMapper;
import com.project.supplychain.models.Product;
import com.project.supplychain.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public HashMap<String, Object> createProduct(ProductDTO dto) {
        log.info("Attempting to create new product with SKU: {}", dto.getSku());

        if(productRepository.findBySku(dto.getSku()).isPresent()){
            log.error("Business Logic Error: Product creation failed. SKU {} already exists in Stock.", dto.getSku());
            throw new BadRequestException("Product with this SKU already exists");
        }

        Product product = productMapper.toEntity(dto);
        product.setId(null);
        Product saved = productRepository.save(product);

        log.info("Product created successfully with ID: {}", saved.getId());

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product created successfully");
        result.put("product", productMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> getProduct(UUID id) {
        log.debug("Fetching product details for ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Business Logic Error: Product lookup failed. Item not found in Warehouse for ID: {}", id);
                    return new BadRequestException("Product not found");
                });

        HashMap<String, Object> result = new HashMap<>();
        result.put("product", productMapper.toDTO(product));
        return result;
    }

    public HashMap<String, Object> getProductBySku(String sku) {
        log.debug("Fetching product details for SKU: {}", sku);

        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> {
                    log.error("Business Logic Error: Product lookup failed. SKU {} not found in Stock.", sku);
                    return new BadRequestException("Product not found");
                });

        HashMap<String, Object> result = new HashMap<>();
        result.put("product", productMapper.toDTO(product));
        return result;
    }

    public HashMap<String, Object> listProducts() {
        log.info("Listing all products from inventory");

        List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();

        log.info("Retrieved {} products", products.size());

        HashMap<String, Object> result = new HashMap<>();
        result.put("products", products);
        return result;
    }

    public HashMap<String, Object> updateProduct(UUID id, ProductDTO dto) {
        log.info("Attempting to update product ID: {}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Business Logic Error: Update failed. Product ID {} not found in Stock.", id);
                    return new BadRequestException("Product not found");
                });

        existing.setSku(dto.getSku());
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setActive(dto.isActive());
        existing.setOriginalPrice(dto.getOriginalPrice());
        existing.setProfit(dto.getProfit());

        Product saved = productRepository.save(existing);

        log.info("Product ID {} updated successfully. New SKU: {}", saved.getId(), saved.getSku());

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product updated successfully");
        result.put("product", productMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> deleteProduct(UUID id) {
        log.warn("Request received to delete product ID: {}", id);

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Business Logic Error: Delete failed. Product ID {} not found in Warehouse.", id);
                    return new BadRequestException("Product not found");
                });

        productRepository.delete(existing);

        log.info("Product ID {} deleted successfully", id);

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product deleted successfully");
        return result;
    }
}