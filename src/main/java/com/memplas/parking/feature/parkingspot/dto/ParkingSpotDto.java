package com.memplas.parking.feature.parkingspot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.memplas.parking.feature.parkingspot.model.SpotStatus;
import com.memplas.parking.feature.parkingspot.model.SpotType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParkingSpotDto(
        @Schema(hidden = true)
        Long id,
        @NotNull(message = "Facility ID is required")
        Long facilityId,
        @NotBlank(message = "Spot number is required")
        @Size(max = 20, message = "Spot number must not exceed 20 characters")
        String spotNumber,
        SpotType spotType,
        Long floorId,
        Integer floorLevel,
        @Size(max = 50, message = "Zone must not exceed 50 characters")
        String zone,
        SpotStatus status,
        Long reservedBy,
        LocalDateTime reservationExpiry,
        LocalDateTime lastOccupiedAt,
        @Schema(hidden = true)
        Instant createdAt,
        @Schema(hidden = true)
        Instant updatedAt
) {}
