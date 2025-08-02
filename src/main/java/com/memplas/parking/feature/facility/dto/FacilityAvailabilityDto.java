package com.memplas.parking.feature.facility.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FacilityAvailabilityDto(
        @NotNull(message = "Facility ID is required")
        Long facilityId,
        String facilityName,
        String address,
        Double locationLat,
        Double locationLng,
        @NotNull(message = "Total spots is required")
        @Min(value = 0, message = "Total spots must be non-negative")
        Integer totalSpots,
        @NotNull(message = "Available spots is required")
        @Min(value = 0, message = "Available spots must be non-negative")
        Integer availableSpots,
        @Min(value = 0, message = "Occupied spots must be non-negative")
        Integer occupiedSpots,
        @Min(value = 0, message = "Reserved spots must be non-negative")
        Integer reservedSpots,
        @Min(value = 0, message = "Out of order spots must be non-negative")
        Integer outOfOrderSpots,
        @NotNull(message = "Current occupancy rate is required")
        @DecimalMin(value = "0.0", message = "Occupancy rate must be between 0 and 100")
        @DecimalMax(value = "100.0", message = "Occupancy rate must be between 0 and 100")
        BigDecimal currentOccupancyRate,
        BigDecimal currentHourlyRate,
        LocalDateTime lastUpdated
) {}
