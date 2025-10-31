package com.project.supplychain.DTOs.carrierDTOs;

import com.project.supplychain.enums.CarrierStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarrierDTO {
    private UUID id;

    @NotBlank(message = "code is required")
    @Size(max = 50, message = "code must be at most 50 characters")
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    private String phone;
    private String baseShippingRate;

    @NotNull(message = "maxDailyCapacity is required")
    @Min(value = 0, message = "maxDailyCapacity must be >= 0")
    private Integer maxDailyCapacity;

    @Min(value = 0, message = "currentDailyShipments must be >= 0")
    private Integer currentDailyShipments;

    private LocalDateTime cutOffTime;

    @NotNull(message = "status is required")
    private CarrierStatus status;
}
