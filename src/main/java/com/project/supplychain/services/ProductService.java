package com.project.supplychain.services;

import com.project.supplychain.DTOs.productDTOs.ProductDTO;
import com.project.supplychain.exceptions.BadRequestException;
import com.project.supplychain.mappers.ProductMapper;
import com.project.supplychain.models.Product;
import com.project.supplychain.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    public HashMap<String, Object> createProduct(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        product.setId(null);
        Product saved = productRepository.save(product);

        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product created successfully");
        result.put("product", productMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        HashMap<String, Object> result = new HashMap<>();
        result.put("product", productMapper.toDTO(product));
        return result;
    }

    public HashMap<String, Object> listProducts() {
        List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(productMapper::toDTO)
                .toList();
        HashMap<String, Object> result = new HashMap<>();
        result.put("products", products);
        return result;
    }

    public HashMap<String, Object> updateProduct(UUID id, ProductDTO dto) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        existing.setSku(dto.getSku());
        existing.setName(dto.getName());
        existing.setCategory(dto.getCategory());
        existing.setActive(dto.isActive());
        existing.setOriginalPrice(dto.getOriginalPrice());
        existing.setProfit(dto.getProfit());

        Product saved = productRepository.save(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product updated successfully");
        result.put("product", productMapper.toDTO(saved));
        return result;
    }

    public HashMap<String, Object> deleteProduct(UUID id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        productRepository.delete(existing);
        HashMap<String, Object> result = new HashMap<>();
        result.put("message", "Product deleted successfully");
        return result;
    }
}
