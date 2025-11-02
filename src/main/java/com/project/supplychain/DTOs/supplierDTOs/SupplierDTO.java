package com.project.supplychain.DTOs.supplierDTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    private UUID id;

    @NotBlank(message = "Supplier name is required")
    private String name;

    @NotBlank(message = "Contact info is required")
    private String contactInfo;
}
