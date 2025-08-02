package com.memplas.parking.feature.vehicle.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memplas.parking.feature.vehicle.model.VehicleType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VehicleDto(
        @Schema(hidden = true)
        Long id,
        @NotNull(message = "User ID is required")
        Long userId,
        @NotBlank(message = "License plate is required")
        @Size(max = 20, message = "License plate must not exceed 20 characters")
        String licensePlate,
        @Size(max = 50, message = "Make must not exceed 50 characters")
        String make,
        @Size(max = 50, message = "Model must not exceed 50 characters")
        String model,
        @Size(max = 30, message = "Color must not exceed 30 characters")
        String color,
        VehicleType vehicleType,
        Boolean isPrimary,
        @Schema(hidden = true)
        Instant createdAt,
        @Schema(hidden = true)
        Instant updatedAt
) {}
