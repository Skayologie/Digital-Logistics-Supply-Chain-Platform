package com.project.supplychain.mappers;

import com.project.supplychain.DTOs.productDTOs.ProductDTO;
import com.project.supplychain.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

	public Product toEntity(ProductDTO dto) {
		if (dto == null) return null;

		return Product.builder()
				.id(dto.getId())
				.sku(dto.getSku())
				.name(dto.getName())
				.category(dto.getCategory())
				.active(dto.isActive())
				.originalPrice(dto.getOriginalPrice())
				.profit(dto.getProfit())
				.build();
	}

	public ProductDTO toDTO(Product product) {
		if (product == null) return null;

		return ProductDTO.builder()
				.id(product.getId())
				.sku(product.getSku())
				.name(product.getName())
				.category(product.getCategory())
				.active(product.isActive())
				.originalPrice(product.getOriginalPrice())
				.profit(product.getProfit())
				.build();
	}
}
